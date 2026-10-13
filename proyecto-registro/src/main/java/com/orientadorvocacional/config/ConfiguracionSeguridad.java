package com.orientadorvocacional.config;

import com.orientadorvocacional.seguridad.InterceptorAutenticacion;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registra el interceptor que exige un token valido en los endpoints
 * que manejan datos del usuario.
 */
@Configuration
public class ConfiguracionSeguridad implements WebMvcConfigurer {

    private final InterceptorAutenticacion interceptorAutenticacion;

    public ConfiguracionSeguridad(InterceptorAutenticacion interceptorAutenticacion) {
        this.interceptorAutenticacion = interceptorAutenticacion;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptorAutenticacion)
                .addPathPatterns(
                        "/api/test/resultado",
                        "/api/perfil/**",
                        "/api/carreras/recomendadas");
    }
}