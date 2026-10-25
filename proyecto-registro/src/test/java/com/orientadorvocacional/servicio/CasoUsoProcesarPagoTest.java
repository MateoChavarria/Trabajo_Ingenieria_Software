package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.OperacionPagoFallidaException;
import com.orientadorvocacional.excepciones.PagoNoEncontradoException;
import com.orientadorvocacional.excepciones.PagoYaProcesadoException;
import com.orientadorvocacional.modelo.DatosTarjeta;
import com.orientadorvocacional.modelo.EstadoPago;
import com.orientadorvocacional.modelo.Pago;
import com.orientadorvocacional.modelo.ResultadoCobro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del proceso que confirma un pago: pago exitoso, pago fallido,
 * pagos ajenos, pagos repetidos y carreras entre dos peticiones.
 */
class CasoUsoProcesarPagoTest {

    private static final String USUARIO = "11111111-1111-1111-1111-111111111111";
    private static final String OTRO_USUARIO = "22222222-2222-2222-2222-222222222222";

    private RepositorioPagosEnMemoria pagos;
    private RepositorioAccesoEnMemoria acceso;
    private int cobrosIntentados;
    private CasoUsoProcesarPago casoUso;
    private Pago pagoPendiente;

    @BeforeEach
    void configurar() throws Exception {
        pagos = new RepositorioPagosEnMemoria();
        acceso = new RepositorioAccesoEnMemoria();
        cobrosIntentados = 0;

        // Pasarela falsa: aprueba solo la tarjeta de prueba 4242..., igual que la simulada.
        IPasarelaPagos pasarela = (tarjeta, monto, referencia) -> {
            cobrosIntentados++;
            return tarjeta.getNumeroTarjeta().equals("4242424242424242")
                    ? ResultadoCobro.aprobado()
                    : ResultadoCobro.rechazado("Fondos insuficientes.");
        };

        casoUso = new CasoUsoProcesarPago(pagos, acceso, pasarela);
        pagoPendiente = new CasoUsoIniciarPago(acceso, pagos).ejecutar(USUARIO);
    }

    private DatosTarjeta tarjeta(String numero) {
        YearMonth futuro = YearMonth.now().plusYears(2);
        String vencimiento = String.format("%02d/%02d", futuro.getMonthValue(), futuro.getYear() % 100);
        return new DatosTarjeta("Ana Perez", numero, vencimiento, "123");
    }

    @Test
    void unPagoExitosoQuedaRegistradoYActivaElAccesoCompleto() throws Exception {
        Pago resultado = casoUso.ejecutar(USUARIO, pagoPendiente.getReferencia(), tarjeta("4242424242424242"));

        assertEquals(EstadoPago.EXITOSO, resultado.getEstado());
        assertEquals(EstadoPago.EXITOSO, pagos.filaGuardada(pagoPendiente.getReferencia()).getEstado());
        assertTrue(acceso.tieneAccesoCompleto(USUARIO));
    }

    @Test
    void unPagoFallidoGuardaElMotivoYNoActivaElAcceso() throws Exception {
        Pago resultado = casoUso.ejecutar(USUARIO, pagoPendiente.getReferencia(), tarjeta("4000000000000002"));

        assertEquals(EstadoPago.FALLIDO, resultado.getEstado());
        assertEquals("Fondos insuficientes.", pagos.filaGuardada(pagoPendiente.getReferencia()).getMotivoFallo());
        assertFalse(acceso.tieneAccesoCompleto(USUARIO));
    }

    @Test
    void despuesDeUnFalloElUsuarioPuedeReintentarYPagar() throws Exception {
        casoUso.ejecutar(USUARIO, pagoPendiente.getReferencia(), tarjeta("4000000000000002"));

        Pago reintento = new CasoUsoIniciarPago(acceso, pagos).ejecutar(USUARIO);
        Pago resultado = casoUso.ejecutar(USUARIO, reintento.getReferencia(), tarjeta("4242424242424242"));

        assertEquals(EstadoPago.EXITOSO, resultado.getEstado());
        assertTrue(acceso.tieneAccesoCompleto(USUARIO));
    }

    @Test
    void nadiePuedePagarElPagoDeOtroUsuario() throws Exception {
        assertThrows(PagoNoEncontradoException.class,
                () -> casoUso.ejecutar(OTRO_USUARIO, pagoPendiente.getReferencia(), tarjeta("4242424242424242")));

        assertEquals(0, cobrosIntentados);
        assertFalse(acceso.tieneAccesoCompleto(OTRO_USUARIO));
    }

    @Test
    void unaReferenciaInexistenteDaPagoNoEncontrado() {
        assertThrows(PagoNoEncontradoException.class,
                () -> casoUso.ejecutar(USUARIO, "PAY-AAAAAAAAAAAA", tarjeta("4242424242424242")));
    }

    @Test
    void unPagoYaProcesadoNoSeCobraDeNuevo() throws Exception {
        casoUso.ejecutar(USUARIO, pagoPendiente.getReferencia(), tarjeta("4242424242424242"));

        assertThrows(PagoYaProcesadoException.class,
                () -> casoUso.ejecutar(USUARIO, pagoPendiente.getReferencia(), tarjeta("4242424242424242")));
        assertEquals(1, cobrosIntentados);
    }

    @Test
    void siOtraPeticionSeAdelantaNoSeActivaElAcceso() throws Exception {
        pagos.otraPeticionSeAdelanto = true;

        assertThrows(PagoYaProcesadoException.class,
                () -> casoUso.ejecutar(USUARIO, pagoPendiente.getReferencia(), tarjeta("4242424242424242")));
        assertFalse(acceso.tieneAccesoCompleto(USUARIO));
    }

    @Test
    void siLaBaseDeDatosFallaSePropagaElError() {
        acceso.baseDeDatosCaida = true;

        assertThrows(OperacionPagoFallidaException.class,
                () -> casoUso.ejecutar(USUARIO, pagoPendiente.getReferencia(), tarjeta("4242424242424242")));
    }
}
