package com.orientadorvocacional.excepciones;

/**
 * Se lanza cuando no se pudo obtener el cuestionario del test vocacional
 * (problema de conexión o respuesta inesperada del servidor).
 */
public class CuestionarioFallidoException extends Exception {

    public CuestionarioFallidoException(String mensaje) {
        super(mensaje);
    }

    public CuestionarioFallidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}