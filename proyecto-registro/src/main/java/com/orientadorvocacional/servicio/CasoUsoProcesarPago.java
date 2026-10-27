package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.OperacionPagoFallidaException;
import com.orientadorvocacional.excepciones.PagoNoEncontradoException;
import com.orientadorvocacional.excepciones.PagoYaProcesadoException;
import com.orientadorvocacional.modelo.DatosTarjeta;
import com.orientadorvocacional.modelo.EstadoPago;
import com.orientadorvocacional.modelo.Pago;
import com.orientadorvocacional.modelo.ResultadoCobro;
import com.orientadorvocacional.repositorio.IRepositorioAcceso;
import com.orientadorvocacional.repositorio.IRepositorioPagos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Proceso que CONFIRMA un pago: manda el cobro a la pasarela, guarda el
 * resultado (exitoso o fallido) y, si fue exitoso, actualiza al usuario
 * con acceso completo.
 *
 * Todo ocurre en una sola transaccion: si no se puede activar el acceso,
 * el resultado del pago tambien se deshace (el pago sigue pendiente), asi
 * nunca queda un usuario que pago sin acceso.
 */
@Service
public class CasoUsoProcesarPago {

    private final IRepositorioPagos repositorioPagos;
    private final IRepositorioAcceso repositorioAcceso;
    private final IPasarelaPagos pasarela;

    public CasoUsoProcesarPago(IRepositorioPagos repositorioPagos,
                               IRepositorioAcceso repositorioAcceso,
                               IPasarelaPagos pasarela) {
        this.repositorioPagos = repositorioPagos;
        this.repositorioAcceso = repositorioAcceso;
        this.pasarela = pasarela;
    }

    @Transactional(rollbackFor = Exception.class)
    public Pago ejecutar(String usuarioId, String referencia, DatosTarjeta tarjeta)
            throws PagoNoEncontradoException, PagoYaProcesadoException, OperacionPagoFallidaException {

        // Se busca por referencia Y por usuario: nadie puede pagar ni ver el pago de otra persona.
        Pago pago = repositorioPagos.buscarPorReferencia(referencia, usuarioId)
                .orElseThrow(() -> new PagoNoEncontradoException("No se encontró el pago."));

        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            throw new PagoYaProcesadoException("Este pago ya fue procesado.");
        }

        ResultadoCobro resultado = pasarela.cobrar(tarjeta, pago.getMonto(), pago.getReferencia());
        if (resultado.isAprobado()) {
            pago.marcarExitoso();
        } else {
            pago.marcarFallido(resultado.getMotivo());
        }

        // Si otra peticion lo proceso al mismo tiempo, esto devuelve false y no se cobra dos veces.
        if (!repositorioPagos.registrarResolucion(pago)) {
            throw new PagoYaProcesadoException("Este pago ya fue procesado.");
        }

        if (pago.getEstado() == EstadoPago.EXITOSO) {
            repositorioAcceso.otorgarAccesoCompleto(usuarioId);
        }
        return pago;
    }
}
