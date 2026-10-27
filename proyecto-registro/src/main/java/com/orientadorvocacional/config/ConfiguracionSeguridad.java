package com.orientadorvocacional.config;

import com.orientadorvocacional.seguridad.InterceptorAccesoCompleto;
import com.orientadorvocacional.seguridad.InterceptorAdmin;
import com.orientadorvocacional.seguridad.InterceptorAutenticacion;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registra los interceptores de seguridad, en este orden:
 *   1) InterceptorAutenticacion: exige un token valido (quien eres).
 *   2) InterceptorAccesoCompleto: exige haber pagado (que puedes usar).
 *   2) InterceptorAdmin: en /api/admin/** solo deja pasar a la cuenta administradora.
 */
@Configuration
public class ConfiguracionSeguridad implements WebMvcConfigurer {

    /** Rutas que exigen haber pagado (acceso completo). */
    static final String[] RUTAS_DE_PAGO = {
            "/api/test/preguntas",
            "/api/test/resultado",
            "/api/perfil/resumen",
            "/api/carreras/recomendadas",
            "/api/carreras/*/simulacion"
    };

    private final InterceptorAutenticacion interceptorAutenticacion;
    private final InterceptorAccesoCompleto interceptorAccesoCompleto;
    private final InterceptorAdmin interceptorAdmin;

    public ConfiguracionSeguridad(InterceptorAutenticacion interceptorAutenticacion,
                                  InterceptorAccesoCompleto interceptorAccesoCompleto,
                                  InterceptorAdmin interceptorAdmin) {
        this.interceptorAutenticacion = interceptorAutenticacion;
        this.interceptorAccesoCompleto = interceptorAccesoCompleto;
        this.interceptorAdmin = interceptorAdmin;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Rutas que exigen sesion: las de pago y las del propio usuario.
        registry.addInterceptor(interceptorAutenticacion)
                .addPathPatterns(
                        "/api/test/preguntas",
                        "/api/test/resultado",
                        "/api/perfil/**",
                        "/api/carreras/recomendadas",
                        "/api/carreras/*/simulacion",
                        "/api/pagos/**",
                        "/api/yo/admin",
                        "/api/admin/**")
                .order(1);

        registry.addInterceptor(interceptorAccesoCompleto)
                .addPathPatterns(RUTAS_DE_PAGO)
                .order(2);

        // Panel de administracion: ademas de la sesion, solo la cuenta administradora.
        registry.addInterceptor(interceptorAdmin)
                .addPathPatterns("/api/admin/**")
                .order(3);
    }
}
