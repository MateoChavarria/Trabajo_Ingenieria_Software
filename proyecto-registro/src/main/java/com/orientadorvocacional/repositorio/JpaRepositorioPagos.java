package com.orientadorvocacional.repositorio;

import com.orientadorvocacional.entidad.PagoEntity;
import com.orientadorvocacional.excepciones.OperacionPagoFallidaException;
import com.orientadorvocacional.modelo.EstadoPago;
import com.orientadorvocacional.modelo.Pago;
import com.orientadorvocacional.repositorio.jpa.PagoJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementacion de IRepositorioPagos con JPA. Traduce entre el modelo
 * de negocio (Pago) y la fila de la base de datos (PagoEntity), y
 * envuelve los fallos tecnicos en OperacionPagoFallidaException para no
 * filtrar detalles de JPA hacia las capas de arriba.
 */
@Repository
public class JpaRepositorioPagos implements IRepositorioPagos {

    private final PagoJpaRepository pagoJpa;

    public JpaRepositorioPagos(PagoJpaRepository pagoJpa) {
        this.pagoJpa = pagoJpa;
    }

    @Override
    public void guardarNuevo(Pago pago) throws OperacionPagoFallidaException {
        try {
            pagoJpa.save(new PagoEntity(
                    UUID.fromString(pago.getUsuarioId()),
                    pago.getMonto(),
                    pago.getEstado().aTextoBaseDeDatos(),
                    pago.getReferencia(),
                    pago.getFechaCreacion(),
                    pago.getFechaResolucion(),
                    pago.getMotivoFallo()));
        } catch (Exception excepcion) {
            throw new OperacionPagoFallidaException("No se pudo iniciar el pago.", excepcion);
        }
    }

    @Override
    public Optional<Pago> buscarPorReferencia(String referencia, String usuarioId)
            throws OperacionPagoFallidaException {
        try {
            return pagoJpa.findByReferenciaAndUsuarioId(referencia, UUID.fromString(usuarioId))
                    .map(this::aPago);
        } catch (Exception excepcion) {
            throw new OperacionPagoFallidaException("No se pudo consultar el pago.", excepcion);
        }
    }

    @Override
    public Optional<Pago> buscarPendienteDeUsuario(String usuarioId) throws OperacionPagoFallidaException {
        try {
            return pagoJpa.findFirstByUsuarioIdAndEstadoOrderByFechaDesc(
                    UUID.fromString(usuarioId), EstadoPago.PENDIENTE.aTextoBaseDeDatos())
                    .map(this::aPago);
        } catch (Exception excepcion) {
            throw new OperacionPagoFallidaException("No se pudo consultar el pago.", excepcion);
        }
    }

    @Override
    @Transactional
    public boolean registrarResolucion(Pago pago) throws OperacionPagoFallidaException {
        try {
            int filas = pagoJpa.resolverSiPendiente(
                    pago.getReferencia(),
                    pago.getEstado().aTextoBaseDeDatos(),
                    pago.getFechaResolucion(),
                    pago.getMotivoFallo());
            return filas == 1;
        } catch (Exception excepcion) {
            throw new OperacionPagoFallidaException("No se pudo registrar el resultado del pago.", excepcion);
        }
    }

    private Pago aPago(PagoEntity entidad) {
        return new Pago(
                entidad.getUsuarioId().toString(),
                entidad.getMonto(),
                entidad.getReferencia(),
                EstadoPago.desdeTextoBaseDeDatos(entidad.getEstado()),
                entidad.getFecha(),
                entidad.getFechaResolucion(),
                entidad.getMotivoFallo());
    }
}
