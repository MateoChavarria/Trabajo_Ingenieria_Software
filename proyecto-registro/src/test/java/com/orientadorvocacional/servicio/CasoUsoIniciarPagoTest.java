package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.AccesoYaOtorgadoException;
import com.orientadorvocacional.excepciones.OperacionPagoFallidaException;
import com.orientadorvocacional.modelo.EstadoPago;
import com.orientadorvocacional.modelo.OfertaAcceso;
import com.orientadorvocacional.modelo.Pago;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas del proceso que inicia un pago. */
class CasoUsoIniciarPagoTest {

    private static final String USUARIO = "11111111-1111-1111-1111-111111111111";

    private RepositorioPagosEnMemoria pagos;
    private RepositorioAccesoEnMemoria acceso;
    private CasoUsoIniciarPago casoUso;

    @BeforeEach
    void configurar() {
        pagos = new RepositorioPagosEnMemoria();
        acceso = new RepositorioAccesoEnMemoria();
        casoUso = new CasoUsoIniciarPago(acceso, pagos);
    }

    @Test
    void creaUnPagoPendienteConElPrecioDeLaOferta() throws Exception {
        Pago pago = casoUso.ejecutar(USUARIO);

        assertEquals(EstadoPago.PENDIENTE, pago.getEstado());
        assertEquals(OfertaAcceso.PRECIO_COP, pago.getMonto());
        assertEquals(20000, pago.getMonto());
        assertEquals(1, pagos.totalPagosGuardados());
    }

    @Test
    void siYaHayUnPagoPendienteSeReutiliza() throws Exception {
        Pago primero = casoUso.ejecutar(USUARIO);
        Pago segundo = casoUso.ejecutar(USUARIO);

        assertEquals(primero.getReferencia(), segundo.getReferencia());
        assertEquals(1, pagos.totalPagosGuardados());
    }

    @Test
    void unPagoYaResueltoNoSeReutiliza() throws Exception {
        Pago primero = casoUso.ejecutar(USUARIO);
        primero.marcarFallido("Fondos insuficientes.");
        pagos.registrarResolucion(primero);

        Pago segundo = casoUso.ejecutar(USUARIO);

        assertNotEquals(primero.getReferencia(), segundo.getReferencia());
        assertEquals(2, pagos.totalPagosGuardados());
    }

    @Test
    void siYaTieneAccesoCompletoNoSeLeCobraDeNuevo() {
        acceso.darAcceso(USUARIO);

        assertThrows(AccesoYaOtorgadoException.class, () -> casoUso.ejecutar(USUARIO));
        assertEquals(0, pagos.totalPagosGuardados());
    }

    @Test
    void siLaBaseDeDatosFallaSePropagaElError() {
        acceso.baseDeDatosCaida = true;

        assertThrows(OperacionPagoFallidaException.class, () -> casoUso.ejecutar(USUARIO));
    }
}
