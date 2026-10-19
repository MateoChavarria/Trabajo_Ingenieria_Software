package com.orientadorvocacional.excepciones;

/** No existe un pago con esa referencia para ese usuario. */
public class PagoNoEncontradoException extends Exception {
    public PagoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
