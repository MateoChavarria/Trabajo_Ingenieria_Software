package com.orientadorvocacional.modelo;

import com.orientadorvocacional.excepciones.PagoYaProcesadoException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas del modelo Pago: estado inicial, validaciones y cambios de estado. */
class PagoTest {

    private static final String USUARIO = "11111111-1111-1111-1111-111111111111";

    @Test
    void unPagoNuevoQuedaPendienteConReferenciaValida() {
        Pago pago = new Pago(USUARIO, 20000);

        assertEquals(EstadoPago.PENDIENTE, pago.getEstado());
        assertEquals(20000, pago.getMonto());
        assertTrue(Pago.esReferenciaValida(pago.getReferencia()));
        assertNotNull(pago.getFechaCreacion());
        assertNull(pago.getFechaResolucion());
        assertNull(pago.getMotivoFallo());
    }

    @Test
    void lasReferenciasGeneradasSonDistintas() {
        assertNotEquals(Pago.generarReferencia(), Pago.generarReferencia());
    }

    @Test
    void soloAceptaReferenciasConElFormatoEsperado() {
        assertTrue(Pago.esReferenciaValida("PAY-0123456789AB"));
        assertFalse(Pago.esReferenciaValida("pay-0123456789ab"));
        assertFalse(Pago.esReferenciaValida("PAY-123"));
        assertFalse(Pago.esReferenciaValida("../../etc/passwd"));
        assertFalse(Pago.esReferenciaValida(null));
    }

    @Test
    void rechazaMontosNoPositivos() {
        assertThrows(IllegalArgumentException.class, () -> new Pago(USUARIO, 0));
        assertThrows(IllegalArgumentException.class, () -> new Pago(USUARIO, -500));
    }

    @Test
    void rechazaUsuarioVacio() {
        assertThrows(IllegalArgumentException.class, () -> new Pago(" ", 20000));
        assertThrows(IllegalArgumentException.class, () -> new Pago(null, 20000));
    }

    @Test
    void marcarExitosoGuardaLaFechaDeResolucion() throws Exception {
        Pago pago = new Pago(USUARIO, 20000);
        pago.marcarExitoso();

        assertEquals(EstadoPago.EXITOSO, pago.getEstado());
        assertNotNull(pago.getFechaResolucion());
    }

    @Test
    void marcarFallidoGuardaElMotivo() throws Exception {
        Pago pago = new Pago(USUARIO, 20000);
        pago.marcarFallido("Fondos insuficientes.");

        assertEquals(EstadoPago.FALLIDO, pago.getEstado());
        assertEquals("Fondos insuficientes.", pago.getMotivoFallo());
        assertNotNull(pago.getFechaResolucion());
    }

    @Test
    void marcarCanceladoCambiaElEstado() throws Exception {
        Pago pago = new Pago(USUARIO, 20000);
        pago.marcarCancelado();

        assertEquals(EstadoPago.CANCELADO, pago.getEstado());
    }

    @Test
    void unPagoYaResueltoNoSePuedeVolverAResolver() throws Exception {
        Pago pago = new Pago(USUARIO, 20000);
        pago.marcarExitoso();

        assertThrows(PagoYaProcesadoException.class, pago::marcarCancelado);
        assertThrows(PagoYaProcesadoException.class, () -> pago.marcarFallido("x"));
        assertThrows(PagoYaProcesadoException.class, pago::marcarExitoso);
        assertEquals(EstadoPago.EXITOSO, pago.getEstado());
    }

    @Test
    void elMotivoMuyLargoSeRecorta() throws Exception {
        Pago pago = new Pago(USUARIO, 20000);
        pago.marcarFallido("a".repeat(500));

        assertEquals(Pago.LONGITUD_MAXIMA_MOTIVO, pago.getMotivoFallo().length());
    }

    @Test
    void reconstruirUnPagoGuardadoConservaSusDatos() {
        LocalDateTime fecha = LocalDateTime.of(2026, 10, 18, 19, 30);
        Pago pago = new Pago(USUARIO, 20000, "PAY-0123456789AB", EstadoPago.FALLIDO, fecha, fecha, "Rechazada");

        assertEquals("PAY-0123456789AB", pago.getReferencia());
        assertEquals(EstadoPago.FALLIDO, pago.getEstado());
        assertEquals("Rechazada", pago.getMotivoFallo());
    }

    @Test
    void elEstadoSeConvierteDeIdaYVueltaConLaBaseDeDatos() {
        for (EstadoPago estado : EstadoPago.values()) {
            assertEquals(estado, EstadoPago.desdeTextoBaseDeDatos(estado.aTextoBaseDeDatos()));
        }
        assertEquals("exitoso", EstadoPago.EXITOSO.aTextoBaseDeDatos());
        assertThrows(IllegalArgumentException.class, () -> EstadoPago.desdeTextoBaseDeDatos(" "));
    }
}
