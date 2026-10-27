package com.orientadorvocacional.seguridad;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class FiltroCabecerasSeguridadTest {

    @Test
    void agregaCabecerasDeSeguridad() throws Exception {
        MockHttpServletResponse respuesta = new MockHttpServletResponse();
        new FiltroCabecerasSeguridad().doFilter(
                new MockHttpServletRequest("GET", "/api/perfil/resumen"), respuesta, new MockFilterChain());

        assertThat(respuesta.getHeader("Content-Security-Policy")).contains("script-src 'self'");
        assertThat(respuesta.getHeader("X-Frame-Options")).isEqualTo("DENY");
        assertThat(respuesta.getHeader("X-Content-Type-Options")).isEqualTo("nosniff");
        assertThat(respuesta.getHeader("Cache-Control")).isEqualTo("no-store");
    }

    @Test
    void catalogoPublicoSePuedeGuardarEnElNavegador() throws Exception {
        for (String ruta : new String[]{"/api/carreras", "/api/carreras/4", "/api/carreras/4/pensum", "/api/universidades"}) {
            MockHttpServletResponse respuesta = new MockHttpServletResponse();
            new FiltroCabecerasSeguridad().doFilter(
                    new MockHttpServletRequest("GET", ruta), respuesta, new MockFilterChain());
            assertThat(respuesta.getHeader("Cache-Control")).isEqualTo("public, max-age=300");
        }
    }

    @Test
    void datosPrivadosOPagadosNuncaSeGuardanEnCache() throws Exception {
        String[][] casos = {
                {"GET", "/api/carreras/4/simulacion"},
                {"GET", "/api/carreras/recomendadas"},
                {"POST", "/api/carreras/recomendadas"},
                {"GET", "/api/test/preguntas"},
                {"GET", "/api/pagos/oferta"},
        };
        for (String[] caso : casos) {
            MockHttpServletResponse respuesta = new MockHttpServletResponse();
            new FiltroCabecerasSeguridad().doFilter(
                    new MockHttpServletRequest(caso[0], caso[1]), respuesta, new MockFilterChain());
            assertThat(respuesta.getHeader("Cache-Control")).as(caso[1]).isEqualTo("no-store");
        }
    }
}
