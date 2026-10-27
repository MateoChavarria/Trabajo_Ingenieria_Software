package com.orientadorvocacional.excepciones;

/**
 * Error de una operacion del panel de administracion. Lleva el codigo HTTP
 * que le corresponde (404 no existe, 409 conflicto, 500 falla interna...).
 */
public class AdminException extends Exception {

    private final int estado;

    public AdminException(int estado, String mensaje) {
        super(mensaje);
        this.estado = estado;
    }

    public AdminException(int estado, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.estado = estado;
    }

    public int getEstado() {
        return estado;
    }

    public static AdminException noEncontrado(String mensaje) {
        return new AdminException(404, mensaje);
    }

    public static AdminException conflicto(String mensaje) {
        return new AdminException(409, mensaje);
    }

    public static AdminException fallaInterna(String mensaje, Throwable causa) {
        return new AdminException(500, mensaje, causa);
    }
}
