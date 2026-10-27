package com.orientadorvocacional.excepciones;

/**
 * Se lanza cuando no se pudieron consultar los temas más difíciles de
 * las materias de una carrera por un problema técnico.
 */
public class ConsultaTemasDificilesFallidaException extends Exception {

    public ConsultaTemasDificilesFallidaException(String mensaje) {
        super(mensaje);
    }

    public ConsultaTemasDificilesFallidaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
