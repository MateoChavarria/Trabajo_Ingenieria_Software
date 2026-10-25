package com.orientadorvocacional.repositorio;

import com.orientadorvocacional.excepciones.OperacionPagoFallidaException;

/**
 * Contrato para consultar y otorgar el "acceso completo" de un usuario
 * (campo acceso_completo de la tabla "usuarios").
 */
public interface IRepositorioAcceso {

    /** true si el usuario ya pago. Si el usuario no existe, devuelve false. */
    boolean tieneAccesoCompleto(String usuarioId) throws OperacionPagoFallidaException;

    /** Marca al usuario con acceso completo. Falla si el usuario no existe. */
    void otorgarAccesoCompleto(String usuarioId) throws OperacionPagoFallidaException;
}
