package com.orientadorvocacional.seguridad;

import com.orientadorvocacional.excepciones.CredencialesInvalidasException;
import com.orientadorvocacional.servicio.IServicioAutenticacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class InterceptorAutenticacionTest {

    private IServicioAutenticacion servicio;
    private InterceptorAutenticacion interceptor;
    private MockHttpServletRequest peticion;
    private MockHttpServletResponse respuesta;

    @BeforeEach
    void configurar() {
        servicio = mock(IServicioAutenticacion.class);
        interceptor = new InterceptorAutenticacion(servicio);
        peticion = new MockHttpServletRequest("GET", "/api/perfil/historial");
        respuesta = new MockHttpServletResponse();
    }

    @Test
    void rechazaPeticionesSinToken() throws Exception {
        assertThat(interceptor.preHandle(peticion, respuesta, null)).isFalse();
        assertThat(respuesta.getStatus()).isEqualTo(401);
        verifyNoInteractions(servicio);
    }

    @Test
    void rechazaTokensInvalidos() throws Exception {
        peticion.addHeader("Authorization", "Bearer token-falso");
        when(servicio.validarToken("token-falso")).thenThrow(new CredencialesInvalidasException("x"));

        assertThat(interceptor.preHandle(peticion, respuesta, null)).isFalse();
        assertThat(respuesta.getStatus()).isEqualTo(401);
    }

    @Test
    void conTokenValidoGuardaElIdDelUsuarioAutenticado() throws Exception {
        peticion.addHeader("Authorization", "Bearer token-bueno");
        when(servicio.validarToken("token-bueno")).thenReturn("id-real");

        assertThat(interceptor.preHandle(peticion, respuesta, null)).isTrue();
        assertThat(peticion.getAttribute(InterceptorAutenticacion.ATRIBUTO_USUARIO_ID)).isEqualTo("id-real");
    }
}