package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.PagoNoEncontradoException;
import com.orientadorvocacional.modelo.OfertaAcceso;
import com.orientadorvocacional.modelo.Pago;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de la oferta de acceso (precio y funciones) y de la consulta de un pago propio. */
class CasoUsoOfertaYConsultaPagoTest {

    private static final String USUARIO = "11111111-1111-1111-1111-111111111111";

    private RepositorioPagosEnMemoria pagos;
    private RepositorioAccesoEnMemoria acceso;

    @BeforeEach
    void configurar() {
        pagos = new RepositorioPagosEnMemoria();
        acceso = new RepositorioAccesoEnMemoria();
    }

    @Test
    void laOfertaMuestraElPrecioYLasFuncionesQueSeDesbloquean() throws Exception {
        OfertaAcceso oferta = new CasoUsoObtenerOfertaAcceso(acceso).ejecutar(USUARIO);

        assertEquals(20000, oferta.getMonto());
        assertEquals("COP", oferta.getMoneda());
        assertFalse(oferta.isAccesoCompleto());
        assertTrue(oferta.getFuncionesDesbloqueadas().contains("Simulador de carrera semestre por semestre"));
        assertTrue(oferta.getFuncionesGratuitas().contains("Ver las universidades"));
    }

    @Test
    void laOfertaIndicaSiElUsuarioYaTieneAcceso() throws Exception {
        acceso.darAcceso(USUARIO);

        assertTrue(new CasoUsoObtenerOfertaAcceso(acceso).ejecutar(USUARIO).isAccesoCompleto());
    }

    @Test
    void elUsuarioPuedeConsultarSuPropioPago() throws Exception {
        Pago pago = new CasoUsoIniciarPago(acceso, pagos).ejecutar(USUARIO);

        Pago consultado = new CasoUsoConsultarPago(pagos).ejecutar(USUARIO, pago.getReferencia());

        assertEquals(pago.getReferencia(), consultado.getReferencia());
    }

    @Test
    void noSePuedeConsultarElPagoDeOtroUsuario() throws Exception {
        Pago pago = new CasoUsoIniciarPago(acceso, pagos).ejecutar(USUARIO);

        assertThrows(PagoNoEncontradoException.class,
                () -> new CasoUsoConsultarPago(pagos).ejecutar("22222222-2222-2222-2222-222222222222",
                        pago.getReferencia()));
    }
}
