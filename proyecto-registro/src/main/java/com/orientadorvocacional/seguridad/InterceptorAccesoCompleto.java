package com.orientadorvocacional.seguridad;

import com.orientadorvocacional.excepciones.OperacionPagoFallidaException;
import com.orientadorvocacional.repositorio.IRepositorioAcceso;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Bloquea en el SERVIDOR las funciones de pago (test, resumen, carreras
 * recomendadas y simulador) para quien todavia no tiene acceso completo.
 *
 * Los candados que se ven en las pantallas son solo visuales: si alguien
 * llamara a estas rutas directamente, este interceptor responde 403.
 *
 * Debe ejecutarse DESPUES de InterceptorAutenticacion, porque necesita
 * el id del usuario que ese interceptor deja en la peticion.
 */
@Component
public class InterceptorAccesoCompleto implements HandlerInterceptor {

    public static final String MENSAJE_ACCESO_REQUERIDO =
            "Esta función requiere acceso completo. Desbloquéala en la pantalla de pago.";

    private final IRepositorioAcceso repositorioAcceso;

    public InterceptorAccesoCompleto(IRepositorioAcceso repositorioAcceso) {
        this.repositorioAcceso = repositorioAcceso;
    }

    @Override
    public boolean preHandle(HttpServletRequest peticion, HttpServletResponse respuesta, Object handler)
            throws IOException {
        if ("OPTIONS".equalsIgnoreCase(peticion.getMethod())) {
            return true;
        }

        Object usuarioId = peticion.getAttribute(InterceptorAutenticacion.ATRIBUTO_USUARIO_ID);
        if (!(usuarioId instanceof String id) || id.isBlank()) {
            return rechazar(respuesta, HttpServletResponse.SC_UNAUTHORIZED,
                    "Debes iniciar sesión para continuar.");
        }

        try {
            if (repositorioAcceso.tieneAccesoCompleto(id)) {
                return true;
            }
            return rechazar(respuesta, HttpServletResponse.SC_FORBIDDEN, MENSAJE_ACCESO_REQUERIDO);

        } catch (OperacionPagoFallidaException excepcion) {
            return rechazar(respuesta, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "No se pudo verificar tu acceso. Intenta más tarde.");
        }
    }

    private boolean rechazar(HttpServletResponse respuesta, int estado, String mensaje) throws IOException {
        respuesta.setStatus(estado);
        respuesta.setContentType(MediaType.TEXT_PLAIN_VALUE);
        respuesta.setCharacterEncoding(StandardCharsets.UTF_8.name());
        respuesta.getWriter().write(mensaje);
        return false;
    }
}
