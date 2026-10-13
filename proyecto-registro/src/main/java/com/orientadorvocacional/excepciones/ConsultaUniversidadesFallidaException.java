package com.orientadorvocacional.excepciones;

/**
 * Se lanza cuando no se pudo consultar la información de universidades
 * (por ejemplo, porque la base de datos no responde).
 */
public class ConsultaUniversidadesFallidaException extends Exception {

    public ConsultaUniversidadesFallidaException(String mensaje) {
        super(mensaje);
    }

    public ConsultaUniversidadesFallidaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
