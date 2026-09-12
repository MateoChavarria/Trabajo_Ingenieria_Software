package com.orientadorvocacional.excepciones;

/**
 * Se lanza cuando el inicio de sesion no se pudo completar por una
 * razon distinta a credenciales invalidas: problema de conexion,
 * respuesta inesperada del servidor, etc.
 */
public class InicioSesionFallidoException extends Exception {

    public InicioSesionFallidoException(String mensaje) {
        super(mensaje);
    }

    public InicioSesionFallidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}