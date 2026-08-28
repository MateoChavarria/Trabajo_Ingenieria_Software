package com.orientadorvocacional.excepciones;

/**
 * Se lanza cuando el correo ingresado esta vacio o no tiene un formato valido.
 */
public class CorreoInvalidoException extends Exception {

    public CorreoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
