package com.orientadorvocacional.seguridad;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

class FiltroLimiteIntentosTest {

    @Test
    void bloqueaAlSuperarElLimiteYSeReiniciaEnLaSiguienteVentana() {
        AtomicLong reloj = new AtomicLong(0);
        FiltroLimiteIntentos filtro = new FiltroLimiteIntentos(3, reloj::get);

        assertThat(filtro.registrarIntento("1.1.1.1")).isTrue();
        assertThat(filtro.registrarIntento("1.1.1.1")).isTrue();
        assertThat(filtro.registrarIntento("1.1.1.1")).isTrue();
        assertThat(filtro.registrarIntento("1.1.1.1")).isFalse();
        assertThat(filtro.registrarIntento("2.2.2.2")).isTrue();

        reloj.set(60_000);
        assertThat(filtro.registrarIntento("1.1.1.1")).isTrue();
    }

    @Test
    void respondeTooManyRequestsEnLoginCuandoSeExcedeElLimite() throws Exception {
        FiltroLimiteIntentos filtro = new FiltroLimiteIntentos(1, () -> 0L);

        MockHttpServletResponse primera = new MockHttpServletResponse();
        filtro.doFilter(new MockHttpServletRequest("POST", "/api/auth/login"), primera, new MockFilterChain());
        MockHttpServletResponse segunda = new MockHttpServletResponse();
        filtro.doFilter(new MockHttpServletRequest("POST", "/api/auth/login"), segunda, new MockFilterChain());

        assertThat(primera.getStatus()).isEqualTo(200);
        assertThat(segunda.getStatus()).isEqualTo(429);
    }
}