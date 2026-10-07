package com.orientadorvocacional.excepciones;

public class ConsultaCarrerasFallidaException extends Exception {
    public ConsultaCarrerasFallidaException(String mensaje) {
        super(mensaje);
    }

    public ConsultaCarrerasFallidaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}