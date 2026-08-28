package com.orientadorvocacional.excepciones;

/**
 * Se lanza cuando la contrasena no cumple las reglas minimas de seguridad
 * (longitud, mayuscula, numero).
 */
public class ContrasenaInvalidaException extends Exception {

    public ContrasenaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
