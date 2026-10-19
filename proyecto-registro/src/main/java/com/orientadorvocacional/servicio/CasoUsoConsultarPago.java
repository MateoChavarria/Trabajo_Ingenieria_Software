package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.OperacionPagoFallidaException;
import com.orientadorvocacional.excepciones.PagoNoEncontradoException;
import com.orientadorvocacional.modelo.Pago;
import com.orientadorvocacional.repositorio.IRepositorioPagos;
import org.springframework.stereotype.Service;

/** Consulta un pago del propio usuario (para la pantalla de confirmacion). */
@Service
public class CasoUsoConsultarPago {

    private final IRepositorioPagos repositorioPagos;

    public CasoUsoConsultarPago(IRepositorioPagos repositorioPagos) {
        this.repositorioPagos = repositorioPagos;
    }

    public Pago ejecutar(String usuarioId, String referencia)
            throws PagoNoEncontradoException, OperacionPagoFallidaException {
        return repositorioPagos.buscarPorReferencia(referencia, usuarioId)
                .orElseThrow(() -> new PagoNoEncontradoException("No se encontró el pago."));
    }
}
