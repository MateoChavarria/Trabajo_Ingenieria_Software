package com.orientadorvocacional.seguridad;

import com.orientadorvocacional.excepciones.OperacionPagoFallidaException;
import com.orientadorvocacional.repositorio.IRepositorioAcceso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas del bloqueo, en el servidor, de las funciones que requieren acceso completo. */
class InterceptorAccesoCompletoTest {

    private static final String USUARIO_CON_ACCESO = "11111111-1111-1111-1111-111111111111";
    private static final String USUARIO_SIN_ACCESO = "22222222-2222-2222-2222-222222222222";

    private boolean baseDeDatosCaida;
    private InterceptorAccesoCompleto interceptor;
    private MockHttpServletRequest peticion;
    private MockHttpServletResponse respuesta;

    @BeforeEach
    void configurar() {
        baseDeDatosCaida = false;
        IRepositorioAcceso repositorio = new IRepositorioAcceso() {
            @Override
            public boolean tieneAccesoCompleto(String usuarioId) throws OperacionPagoFallidaException {
                if (baseDeDatosCaida) {
                    throw new OperacionPagoFallidaException("caida");
                }
                return USUARIO_CON_ACCESO.equals(usuarioId);
            }

            @Override
            public void otorgarAccesoCompleto(String usuarioId) {
            }
        };
        interceptor = new InterceptorAccesoCompleto(repositorio);
        peticion = new MockHttpServletRequest("GET", "/api/carreras/5/simulacion");
        respuesta = new MockHttpServletResponse();
    }

    @Test
    void quienPagoPuedePasar() throws Exception {
        peticion.setAttribute(InterceptorAutenticacion.ATRIBUTO_USUARIO_ID, USUARIO_CON_ACCESO);

        assertTrue(interceptor.preHandle(peticion, respuesta, null));
    }

    @Test
    void quienNoHaPagadoRecibe403ConUnMensajeClaro() throws Exception {
        peticion.setAttribute(InterceptorAutenticacion.ATRIBUTO_USUARIO_ID, USUARIO_SIN_ACCESO);

        assertFalse(interceptor.preHandle(peticion, respuesta, null));
        assertEquals(403, respuesta.getStatus());
        assertEquals(InterceptorAccesoCompleto.MENSAJE_ACCESO_REQUERIDO, respuesta.getContentAsString());
    }

    @Test
    void sinUsuarioAutenticadoRecibe401() throws Exception {
        assertFalse(interceptor.preHandle(peticion, respuesta, null));
        assertEquals(401, respuesta.getStatus());
    }

    @Test
    void siNoSePuedeVerificarElAccesoRecibe503YNoSeDejaPasar() throws Exception {
        baseDeDatosCaida = true;
        peticion.setAttribute(InterceptorAutenticacion.ATRIBUTO_USUARIO_ID, USUARIO_CON_ACCESO);

        assertFalse(interceptor.preHandle(peticion, respuesta, null));
        assertEquals(503, respuesta.getStatus());
    }

    @Test
    void laPeticionOptionsDeCorsNoSeBloquea() throws Exception {
        peticion = new MockHttpServletRequest("OPTIONS", "/api/carreras/5/simulacion");

        assertTrue(interceptor.preHandle(peticion, respuesta, null));
    }
}
