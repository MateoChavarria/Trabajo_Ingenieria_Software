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
                new MockHttpServletRequest("GET", "/api/carreras"), respuesta, new MockFilterChain());

        assertThat(respuesta.getHeader("Content-Security-Policy")).contains("script-src 'self'");
        assertThat(respuesta.getHeader("X-Frame-Options")).isEqualTo("DENY");
        assertThat(respuesta.getHeader("X-Content-Type-Options")).isEqualTo("nosniff");
        assertThat(respuesta.getHeader("Cache-Control")).isEqualTo("no-store");
    }
}