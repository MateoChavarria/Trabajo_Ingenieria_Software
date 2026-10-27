package com.orientadorvocacional.seguridad;

import com.orientadorvocacional.excepciones.AdminException;
import com.orientadorvocacional.servicio.ServicioAdmin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Deja pasar a /api/admin/** SOLO a la cuenta administradora. El boton del
 * menu es solo visual: esta es la proteccion real, y se comprueba en cada
 * peticion en el servidor. Si algo falla, se cierra el paso (nunca se abre).
 *
 * Debe ejecutarse DESPUES de InterceptorAutenticacion.
 */
@Component
public class InterceptorAdmin implements HandlerInterceptor {

    public static final String MENSAJE_SIN_PERMISO = "No tienes permiso para esta sección.";

    private final ServicioAdmin servicioAdmin;

    public InterceptorAdmin(ServicioAdmin servicioAdmin) {
        this.servicioAdmin = servicioAdmin;
    }

    @Override
    public boolean preHandle(HttpServletRequest peticion, HttpServletResponse respuesta, Object handler)
            throws IOException {
        if ("OPTIONS".equalsIgnoreCase(peticion.getMethod())) {
            return true;
        }

        Object usuarioId = peticion.getAttribute(InterceptorAutenticacion.ATRIBUTO_USUARIO_ID);
        if (!(usuarioId instanceof String id) || id.isBlank()) {
            return rechazar(respuesta, HttpServletResponse.SC_UNAUTHORIZED, "Debes iniciar sesión para continuar.");
        }

        try {
            if (servicioAdmin.esAdministrador(id)) {
                return true;
            }
            return rechazar(respuesta, HttpServletResponse.SC_FORBIDDEN, MENSAJE_SIN_PERMISO);

        } catch (AdminException excepcion) {
            return rechazar(respuesta, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "No se pudo verificar tu permiso. Intenta más tarde.");
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
