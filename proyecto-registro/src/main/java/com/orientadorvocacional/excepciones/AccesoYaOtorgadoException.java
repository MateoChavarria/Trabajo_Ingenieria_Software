package com.orientadorvocacional.excepciones;

/** El usuario ya tiene acceso completo: no tiene sentido cobrarle otra vez. */
public class AccesoYaOtorgadoException extends Exception {
    public AccesoYaOtorgadoException(String mensaje) {
        super(mensaje);
    }
}
