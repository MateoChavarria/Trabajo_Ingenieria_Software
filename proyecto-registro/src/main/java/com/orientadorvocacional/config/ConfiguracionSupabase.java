package com.orientadorvocacional.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Guarda las credenciales necesarias para llamar a la API de
 * autenticacion de Supabase (Supabase Auth).
 *
 * Los valores se leen de application.properties, que a su vez los toma
 * de variables de entorno (SUPABASE_URL, SUPABASE_ANON_KEY). Nunca se
 * deben escribir credenciales reales en archivos versionados.
 *
 * NOTA IMPORTANTE: esta clase NO se usa para hablar con las tablas de
 * la base de datos. Esa conexion es directa por JDBC (Spring Data JPA).
 * Esta clase solo sirve para registro, inicio de sesion y validacion
 * de tokens, que pasan por la API de Supabase Auth.
 */
@Component
public class ConfiguracionSupabase {

    @Value("${supabase.url}")
    private String urlProyecto;

    @Value("${supabase.anonKey}")
    private String claveAnonPublica;

    /**
     * Falla al arrancar si falta la configuracion o si la URL no usa
     * HTTPS: las contraseñas viajan a Supabase en esas peticiones y no
     * deben enviarse nunca sin cifrar.
     */
    @PostConstruct
    void validar() {
        if (urlProyecto == null || urlProyecto.isBlank()) {
            throw new IllegalStateException("Falta configurar supabase.url");
        }
        if (claveAnonPublica == null || claveAnonPublica.isBlank()) {
            throw new IllegalStateException("Falta configurar supabase.anonKey");
        }
        urlProyecto = urlProyecto.trim().replaceAll("/+$", "");
        if (!esUrlSegura(urlProyecto)) {
            throw new IllegalStateException("supabase.url debe usar https://");
        }
    }

    static boolean esUrlSegura(String url) {
        return url.startsWith("https://")
                || url.startsWith("http://localhost")
                || url.startsWith("http://127.0.0.1");
    }

    public String getUrlProyecto() {
        return urlProyecto;
    }

    public String getClaveAnonPublica() {
        return claveAnonPublica;
    }
}