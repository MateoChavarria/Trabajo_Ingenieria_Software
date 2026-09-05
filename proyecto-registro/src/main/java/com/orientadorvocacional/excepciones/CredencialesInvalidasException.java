package com.orientadorvocacional.excepciones;

/**
 * Se lanza cuando el correo o la contraseña son incorrectos.
 * A propósito, el mensaje NUNCA dice cuál de los dos falló
 * (por seguridad, para no darle pistas a quien intente adivinar).
 */
public class CredencialesInvalidasException extends Exception {

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}