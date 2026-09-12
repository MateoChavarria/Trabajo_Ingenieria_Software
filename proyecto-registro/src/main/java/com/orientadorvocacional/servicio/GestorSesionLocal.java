package com.orientadorvocacional.servicio;

import com.orientadorvocacional.modelo.SesionUsuario;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Properties;

/**
 * Guarda y recupera la sesion activa en un archivo local del usuario,
 * para no pedirle iniciar sesion cada vez que abre la aplicacion
 * (equivalente, en una app de escritorio, a "guardar la sesion en
 * el navegador").
 */
public class GestorSesionLocal {

    private static final String NOMBRE_ARCHIVO = "sesion_local.properties";

    public void guardar(SesionUsuario sesion) throws IOException {
        Properties propiedades = new Properties();
        propiedades.setProperty("token", sesion.getTokenAcceso());
        propiedades.setProperty("idUsuario", sesion.getIdUsuario());
        propiedades.setProperty("fechaExpiracion", sesion.getFechaExpiracion().toString());

        try (OutputStream salida = new FileOutputStream(NOMBRE_ARCHIVO)) {
            propiedades.store(salida, "Sesión local del Orientador Vocacional UPB");
        }
    }

    /**
     * Recupera la sesión guardada, si existe y todavía no expiró.
     * Devuelve null si no hay sesión guardada o si ya venció
     * (en cuyo caso, se le pedirá iniciar sesión de nuevo).
     */
    public SesionUsuario recuperar() {
        File archivo = new File(NOMBRE_ARCHIVO);
        if (!archivo.exists()) {
            return null;
        }

        try (InputStream entrada = new FileInputStream(archivo)) {
            Properties propiedades = new Properties();
            propiedades.load(entrada);

            String token = propiedades.getProperty("token");
            String idUsuario = propiedades.getProperty("idUsuario");
            LocalDateTime fechaExpiracion =
                    LocalDateTime.parse(propiedades.getProperty("fechaExpiracion"));

            if (LocalDateTime.now().isAfter(fechaExpiracion)) {
                cerrarSesion();
                return null;
            }

            SesionUsuario sesion = new SesionUsuario(token, idUsuario);
            return sesion;

        } catch (Exception excepcion) {
            return null;
        }
    }

    public void cerrarSesion() {
        new File(NOMBRE_ARCHIVO).delete();
    }
}