package com.orientadorvocacional.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * CORS (Cross-Origin Resource Sharing).
 *
 * El frontend se sirve DESDE el mismo Spring Boot (carpeta static/),
 * asi que por defecto no se habilita ningun origen externo. Si el
 * equipo separa el frontend en otro servidor, debe listar sus origenes
 * exactos en la propiedad app.cors.allowed-origins (separados por coma).
 * Nunca usar "*": permitiria que cualquier sitio llame a la API.
 */
@Configuration
public class ConfiguracionCors implements WebMvcConfigurer {

    private final String[] origenesPermitidos;

    public ConfiguracionCors(@Value("${app.cors.allowed-origins:}") String origenesPermitidos) {
        this.origenesPermitidos = Arrays.stream(origenesPermitidos.split(","))
                .map(String::trim)
                .filter(origen -> !origen.isEmpty() && !origen.equals("*"))
                .toArray(String[]::new);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (origenesPermitidos.length == 0) {
            return;
        }
        registry.addMapping("/api/**")
                .allowedOrigins(origenesPermitidos)
                .allowedMethods("GET", "POST")
                .allowedHeaders("Content-Type", "Authorization")
                .allowCredentials(false)
                .maxAge(3600);
    }
}