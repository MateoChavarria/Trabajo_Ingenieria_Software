package com.orientadorvocacional.excepciones;

/**
 * Se lanza cuando no se pudo calcular o guardar el resultado del test
 * vocacional (problema de conexion, respuestas invalidas, etc.).
 */
public class ResultadoTestFallidoException extends Exception {

    public ResultadoTestFallidoException(String mensaje) {
        super(mensaje);
    }

    public ResultadoTestFallidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
