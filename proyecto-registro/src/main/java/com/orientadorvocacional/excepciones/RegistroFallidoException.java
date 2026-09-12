package com.orientadorvocacional.excepciones;

/**
 * Se lanza cuando el registro no se pudo completar por una razon
 * distinta a un dato invalido o un correo repetido: por ejemplo,
 * un problema de conexion o una respuesta inesperada del servidor.
 */
public class RegistroFallidoException extends Exception {

    public RegistroFallidoException(String mensaje) {
        super(mensaje);
    }

    public RegistroFallidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
