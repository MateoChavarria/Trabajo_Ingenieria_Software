package com.orientadorvocacional.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Guarda las credenciales necesarias para llamar a la API de
 * autenticacion de Supabase (Supabase Auth).
 *
 * A diferencia de la version anterior (que leia application.properties
 * a mano con la clase Properties de Java), aqui dejamos que Spring lea
 * los valores por nosotros con la anotacion @Value, tomandolos del
 * archivo src/main/resources/application.properties.
 *
 * NOTA IMPORTANTE: esta clase ya NO se usa para hablar con las tablas
 * de la base de datos (usuarios, preguntas, opciones_respuesta,
 * resultados_test). Esa conexion ahora es directa por JDBC, usando
 * Spring Data JPA (ver src/main/resources/application.properties,
 * seccion "spring.datasource.*"). Esta clase solo sirve para el
 * registro y el inicio de sesion, que siguen pasando por la API de
 * Supabase Auth.
 */
@Component
public class ConfiguracionSupabase {

    @Value("${supabase.url}")
    private String urlProyecto;

    @Value("${supabase.anonKey}")
    private String claveAnonPublica;

    public String getUrlProyecto() {
        return urlProyecto;
    }

    public String getClaveAnonPublica() {
        return claveAnonPublica;
    }
}
