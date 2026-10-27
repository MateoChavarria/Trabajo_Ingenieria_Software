package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.OperacionPagoFallidaException;
import com.orientadorvocacional.excepciones.PagoNoEncontradoException;
import com.orientadorvocacional.excepciones.PagoYaProcesadoException;
import com.orientadorvocacional.modelo.Pago;
import com.orientadorvocacional.repositorio.IRepositorioPagos;
import org.springframework.stereotype.Service;

/** Maneja el caso en que el usuario sale de la pasarela sin pagar: el pago queda "cancelado" y no se cobra nada. */
@Service
public class CasoUsoCancelarPago {

    private final IRepositorioPagos repositorioPagos;

    public CasoUsoCancelarPago(IRepositorioPagos repositorioPagos) {
        this.repositorioPagos = repositorioPagos;
    }

    public Pago ejecutar(String usuarioId, String referencia)
            throws PagoNoEncontradoException, PagoYaProcesadoException, OperacionPagoFallidaException {
        Pago pago = repositorioPagos.buscarPorReferencia(referencia, usuarioId)
                .orElseThrow(() -> new PagoNoEncontradoException("No se encontró el pago."));

        pago.marcarCancelado();

        if (!repositorioPagos.registrarResolucion(pago)) {
            throw new PagoYaProcesadoException("Este pago ya fue procesado.");
        }
        return pago;
    }
}
