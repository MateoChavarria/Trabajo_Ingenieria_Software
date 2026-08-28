package com.orientadorvocacional.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Carga las credenciales de conexion a Supabase desde un archivo
 * de propiedades, para no dejar la URL ni la llave escritas
 * directamente ("quemadas") en el codigo fuente.
 */
public class ConfiguracionSupabase {

    private final String urlProyecto;
    private final String claveAnonPublica;

    public ConfiguracionSupabase(String nombreArchivoPropiedades) throws IOException {
        Properties propiedades = new Properties();
        try (InputStream entrada = ConfiguracionSupabase.class
                .getClassLoader()
                .getResourceAsStream(nombreArchivoPropiedades)) {

            if (entrada == null) {
                throw new IOException(
                        "No se encontró el archivo de configuración: " + nombreArchivoPropiedades);
            }
            propiedades.load(entrada);
        }

        this.urlProyecto = propiedades.getProperty("supabase.url");
        this.claveAnonPublica = propiedades.getProperty("supabase.anonKey");

        if (urlProyecto == null || urlProyecto.isBlank()
                || claveAnonPublica == null || claveAnonPublica.isBlank()) {
            throw new IOException(
                    "Faltan datos en application.properties: revisen supabase.url y supabase.anonKey.");
        }
    }

    public String getUrlProyecto() {
        return urlProyecto;
    }

    public String getClaveAnonPublica() {
        return claveAnonPublica;
    }
}
