package com.orientadorvocacional.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Habilita CORS (Cross-Origin Resource Sharing) para que el navegador
 * permita que las paginas HTML/JS le hagan peticiones fetch() a este
 * backend, incluso si en desarrollo llegaran a correr en puertos
 * distintos.
 *
 * Como el frontend en este proyecto se sirve DESDE el mismo Spring
 * Boot (carpeta src/main/resources/static/), en producción esto ni
 * siquiera seria estrictamente necesario — pero se deja activo por si
 * el equipo decide separar el frontend en otro servidor mas adelante.
 */
@Configuration
public class ConfiguracionCors implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
