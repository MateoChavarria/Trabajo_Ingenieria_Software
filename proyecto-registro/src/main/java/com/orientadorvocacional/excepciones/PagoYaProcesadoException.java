package com.orientadorvocacional.excepciones;

/** El pago ya tiene un resultado (exitoso, fallido o cancelado) y no se puede volver a procesar. */
public class PagoYaProcesadoException extends Exception {
    public PagoYaProcesadoException(String mensaje) {
        super(mensaje);
    }
}
