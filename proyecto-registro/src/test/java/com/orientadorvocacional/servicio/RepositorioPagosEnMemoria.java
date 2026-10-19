package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.OperacionPagoFallidaException;
import com.orientadorvocacional.modelo.EstadoPago;
import com.orientadorvocacional.modelo.Pago;
import com.orientadorvocacional.repositorio.IRepositorioPagos;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Repositorio de pagos "falso" para las pruebas: guarda COPIAS en un mapa,
 * como lo haria una base de datos, y registrarResolucion() solo acepta
 * pagos que siguen pendientes (igual que la instruccion SQL real).
 */
class RepositorioPagosEnMemoria implements IRepositorioPagos {

    private final Map<String, Pago> filas = new LinkedHashMap<>();

    /** Simula que otra peticion se adelanto y ya resolvio el pago. */
    boolean otraPeticionSeAdelanto = false;

    int totalPagosGuardados() {
        return filas.size();
    }

    Pago filaGuardada(String referencia) {
        return copiar(filas.get(referencia));
    }

    @Override
    public void guardarNuevo(Pago pago) throws OperacionPagoFallidaException {
        filas.put(pago.getReferencia(), copiar(pago));
    }

    @Override
    public Optional<Pago> buscarPorReferencia(String referencia, String usuarioId) {
        Pago fila = filas.get(referencia);
        if (fila == null || !fila.getUsuarioId().equals(usuarioId)) {
            return Optional.empty();
        }
        return Optional.of(copiar(fila));
    }

    @Override
    public Optional<Pago> buscarPendienteDeUsuario(String usuarioId) {
        return filas.values().stream()
                .filter(p -> p.getUsuarioId().equals(usuarioId) && p.getEstado() == EstadoPago.PENDIENTE)
                .findFirst()
                .map(this::copiar);
    }

    @Override
    public boolean registrarResolucion(Pago pago) {
        Pago fila = filas.get(pago.getReferencia());
        if (otraPeticionSeAdelanto || fila == null || fila.getEstado() != EstadoPago.PENDIENTE) {
            return false;
        }
        filas.put(pago.getReferencia(), copiar(pago));
        return true;
    }

    private Pago copiar(Pago p) {
        return new Pago(p.getUsuarioId(), p.getMonto(), p.getReferencia(), p.getEstado(),
                p.getFechaCreacion(), p.getFechaResolucion(), p.getMotivoFallo());
    }
}
