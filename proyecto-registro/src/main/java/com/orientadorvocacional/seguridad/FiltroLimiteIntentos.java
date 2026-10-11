package com.orientadorvocacional.seguridad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

/**
 * Limita cuantas peticiones de registro / inicio de sesion puede hacer
 * una misma IP por minuto, para frenar ataques de fuerza bruta y el
 * abuso del registro (creacion masiva de cuentas).
 *
 * Es un limitador en memoria (ventana fija): suficiente para una sola
 * instancia del servidor. Si se despliegan varias instancias, conviene
 * moverlo a un almacenamiento compartido (por ejemplo Redis).
 */
@Component
public class FiltroLimiteIntentos extends OncePerRequestFilter {

    private static final String PREFIJO_PROTEGIDO = "/api/auth/";
    private static final long VENTANA_MS = 60_000;
    private static final int MAXIMO_IPS_EN_MEMORIA = 50_000;

    private static final class Ventana {
        private final long inicioMs;
        private int intentos;

        private Ventana(long inicioMs) {
            this.inicioMs = inicioMs;
        }
    }

    private final int maximoIntentosPorVentana;
    private final LongSupplier reloj;
    private final Map<String, Ventana> ventanasPorIp = new ConcurrentHashMap<>();

    @Autowired
    public FiltroLimiteIntentos(@Value("${app.seguridad.intentos-auth-por-minuto:10}") int maximoIntentosPorVentana) {
        this(maximoIntentosPorVentana, System::currentTimeMillis);
    }

    FiltroLimiteIntentos(int maximoIntentosPorVentana, LongSupplier reloj) {
        this.maximoIntentosPorVentana = maximoIntentosPorVentana;
        this.reloj = reloj;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest peticion) {
        return !"POST".equalsIgnoreCase(peticion.getMethod())
                || !peticion.getRequestURI().startsWith(PREFIJO_PROTEGIDO);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest peticion, HttpServletResponse respuesta,
                                    FilterChain cadena) throws ServletException, IOException {
        if (!registrarIntento(peticion.getRemoteAddr())) {
            respuesta.setStatus(429);
            respuesta.setHeader("Retry-After", String.valueOf(VENTANA_MS / 1000));
            respuesta.setContentType(MediaType.TEXT_PLAIN_VALUE);
            respuesta.setCharacterEncoding(StandardCharsets.UTF_8.name());
            respuesta.getWriter().write("Demasiados intentos. Espera un minuto e inténtalo de nuevo.");
            return;
        }
        cadena.doFilter(peticion, respuesta);
    }

    boolean registrarIntento(String ip) {
        long ahora = reloj.getAsLong();
        if (ventanasPorIp.size() > MAXIMO_IPS_EN_MEMORIA) {
            ventanasPorIp.values().removeIf(ventana -> ahora - ventana.inicioMs >= VENTANA_MS);
        }

        Ventana ventana = ventanasPorIp.compute(ip, (clave, actual) ->
                (actual == null || ahora - actual.inicioMs >= VENTANA_MS) ? new Ventana(ahora) : actual);

        synchronized (ventana) {
            ventana.intentos++;
            return ventana.intentos <= maximoIntentosPorVentana;
        }
    }
}