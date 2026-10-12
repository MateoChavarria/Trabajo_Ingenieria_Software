package com.orientadorvocacional.excepciones;

/**
 * Se lanza cuando no se pudo consultar el plan de estudios (pensum)
 * de una carrera por un problema técnico.
 */
public class ConsultaPensumFallidaException extends Exception {

    public ConsultaPensumFallidaException(String mensaje) {
        super(mensaje);
    }

    public ConsultaPensumFallidaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
