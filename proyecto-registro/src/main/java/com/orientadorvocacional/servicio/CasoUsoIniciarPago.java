package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.AccesoYaOtorgadoException;
import com.orientadorvocacional.excepciones.OperacionPagoFallidaException;
import com.orientadorvocacional.modelo.OfertaAcceso;
import com.orientadorvocacional.modelo.Pago;
import com.orientadorvocacional.repositorio.IRepositorioAcceso;
import com.orientadorvocacional.repositorio.IRepositorioPagos;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Proceso que INICIA un pago: crea un registro en estado "pendiente" con
 * su referencia unica. El cobro en si ocurre despues, en la pasarela
 * (CasoUsoProcesarPago).
 *
 * Reglas:
 *   - si el usuario ya tiene acceso completo, no se le cobra otra vez;
 *   - si dejo un pago pendiente sin terminar, se reutiliza ese mismo
 *     (asi no se acumulan registros por pulsar "Pagar" varias veces).
 */
@Service
public class CasoUsoIniciarPago {

    private final IRepositorioAcceso repositorioAcceso;
    private final IRepositorioPagos repositorioPagos;

    public CasoUsoIniciarPago(IRepositorioAcceso repositorioAcceso, IRepositorioPagos repositorioPagos) {
        this.repositorioAcceso = repositorioAcceso;
        this.repositorioPagos = repositorioPagos;
    }

    public Pago ejecutar(String usuarioId) throws AccesoYaOtorgadoException, OperacionPagoFallidaException {
        if (repositorioAcceso.tieneAccesoCompleto(usuarioId)) {
            throw new AccesoYaOtorgadoException("Ya tienes acceso completo. No necesitas pagar de nuevo.");
        }

        Optional<Pago> pendiente = repositorioPagos.buscarPendienteDeUsuario(usuarioId);
        if (pendiente.isPresent()) {
            return pendiente.get();
        }

        Pago nuevo = new Pago(usuarioId, OfertaAcceso.PRECIO_COP);
        repositorioPagos.guardarNuevo(nuevo);
        return nuevo;
    }
}
