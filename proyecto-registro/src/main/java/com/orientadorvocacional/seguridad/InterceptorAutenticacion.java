package com.orientadorvocacional.seguridad;

import com.orientadorvocacional.excepciones.CredencialesInvalidasException;
import com.orientadorvocacional.excepciones.InicioSesionFallidoException;
import com.orientadorvocacional.servicio.IServicioAutenticacion;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Exige la cabecera "Authorization: Bearer <token>" en los endpoints
 * protegidos y verifica el token con el proveedor de autenticacion.
 *
 * El id del usuario autenticado se guarda como atributo de la peticion
 * (ATRIBUTO_USUARIO_ID). Los controladores deben usar ESE id y nunca
 * uno enviado por el cliente, para que nadie pueda leer o escribir
 * datos de otra persona.
 */
@Component
public class InterceptorAutenticacion implements HandlerInterceptor {

    public static final String ATRIBUTO_USUARIO_ID = "usuarioAutenticadoId";

    private static final String PREFIJO_BEARER = "Bearer ";
    private static final int LONGITUD_MAXIMA_TOKEN = 4096;

    private final IServicioAutenticacion servicioAutenticacion;

    public InterceptorAutenticacion(IServicioAutenticacion servicioAutenticacion) {
        this.servicioAutenticacion = servicioAutenticacion;
    }

    @Override
    public boolean preHandle(HttpServletRequest peticion, HttpServletResponse respuesta, Object handler)
            throws IOException {
        if ("OPTIONS".equalsIgnoreCase(peticion.getMethod())) {
            return true;
        }

        String cabecera = peticion.getHeader(HttpHeaders.AUTHORIZATION);
        if (cabecera == null || !cabecera.startsWith(PREFIJO_BEARER)) {
            return rechazar(respuesta, HttpServletResponse.SC_UNAUTHORIZED,
                    "Debes iniciar sesión para continuar.");
        }

        String token = cabecera.substring(PREFIJO_BEARER.length()).trim();
        if (token.isEmpty() || token.length() > LONGITUD_MAXIMA_TOKEN) {
            return rechazar(respuesta, HttpServletResponse.SC_UNAUTHORIZED,
                    "Tu sesión no es válida. Inicia sesión de nuevo.");
        }

        try {
            String idUsuario = servicioAutenticacion.validarToken(token);
            peticion.setAttribute(ATRIBUTO_USUARIO_ID, idUsuario);
            return true;

        } catch (CredencialesInvalidasException excepcion) {
            return rechazar(respuesta, HttpServletResponse.SC_UNAUTHORIZED,
                    "Tu sesión no es válida o expiró. Inicia sesión de nuevo.");
        } catch (InicioSesionFallidoException excepcion) {
            return rechazar(respuesta, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "No se pudo verificar tu sesión. Intenta más tarde.");
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