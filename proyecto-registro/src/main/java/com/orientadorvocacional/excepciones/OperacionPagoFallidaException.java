package com.orientadorvocacional.excepciones;

/** Fallo tecnico (por ejemplo, la base de datos no responde) al manejar pagos o el acceso del usuario. */
public class OperacionPagoFallidaException extends Exception {
    public OperacionPagoFallidaException(String mensaje) {
        super(mensaje);
    }

    public OperacionPagoFallidaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
