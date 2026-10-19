package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.PagoNoEncontradoException;
import com.orientadorvocacional.excepciones.PagoYaProcesadoException;
import com.orientadorvocacional.modelo.EstadoPago;
import com.orientadorvocacional.modelo.Pago;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas del caso en que el usuario cancela el pago en la pasarela. */
class CasoUsoCancelarPagoTest {

    private static final String USUARIO = "11111111-1111-1111-1111-111111111111";

    private RepositorioPagosEnMemoria pagos;
    private RepositorioAccesoEnMemoria acceso;
    private CasoUsoCancelarPago casoUso;
    private Pago pagoPendiente;

    @BeforeEach
    void configurar() throws Exception {
        pagos = new RepositorioPagosEnMemoria();
        acceso = new RepositorioAccesoEnMemoria();
        casoUso = new CasoUsoCancelarPago(pagos);
        pagoPendiente = new CasoUsoIniciarPago(acceso, pagos).ejecutar(USUARIO);
    }

    @Test
    void cancelarDejaElPagoCanceladoSinDarAcceso() throws Exception {
        Pago resultado = casoUso.ejecutar(USUARIO, pagoPendiente.getReferencia());

        assertEquals(EstadoPago.CANCELADO, resultado.getEstado());
        assertEquals(EstadoPago.CANCELADO, pagos.filaGuardada(pagoPendiente.getReferencia()).getEstado());
        assertFalse(acceso.tieneAccesoCompleto(USUARIO));
    }

    @Test
    void noSePuedeCancelarUnPagoYaResuelto() throws Exception {
        casoUso.ejecutar(USUARIO, pagoPendiente.getReferencia());

        assertThrows(PagoYaProcesadoException.class,
                () -> casoUso.ejecutar(USUARIO, pagoPendiente.getReferencia()));
    }

    @Test
    void noSePuedeCancelarElPagoDeOtroUsuario() {
        assertThrows(PagoNoEncontradoException.class,
                () -> casoUso.ejecutar("22222222-2222-2222-2222-222222222222", pagoPendiente.getReferencia()));
    }

    @Test
    void siOtraPeticionSeAdelantaNoSePisaSuResultado() {
        pagos.otraPeticionSeAdelanto = true;

        assertThrows(PagoYaProcesadoException.class,
                () -> casoUso.ejecutar(USUARIO, pagoPendiente.getReferencia()));
    }
}
