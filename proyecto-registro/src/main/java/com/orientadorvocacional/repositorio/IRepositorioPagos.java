package com.orientadorvocacional.repositorio;

import com.orientadorvocacional.excepciones.OperacionPagoFallidaException;
import com.orientadorvocacional.modelo.Pago;

import java.util.Optional;

/** Contrato para guardar y consultar los pagos (tabla "pagos"). */
public interface IRepositorioPagos {

    /** Guarda un pago nuevo (estado pendiente). */
    void guardarNuevo(Pago pago) throws OperacionPagoFallidaException;

    /** Busca un pago por referencia, solo si es de ese usuario. */
    Optional<Pago> buscarPorReferencia(String referencia, String usuarioId) throws OperacionPagoFallidaException;

    /** El pago pendiente mas reciente del usuario, si tiene alguno. */
    Optional<Pago> buscarPendienteDeUsuario(String usuarioId) throws OperacionPagoFallidaException;

    /**
     * Guarda el resultado del pago (exitoso, fallido o cancelado).
     *
     * @return false si el pago ya no estaba pendiente (alguien lo proceso antes)
     */
    boolean registrarResolucion(Pago pago) throws OperacionPagoFallidaException;
}
