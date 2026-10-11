package com.orientadorvocacional.servicio;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidadorAfinidadTest {

    @Test
    void aceptaUnMapaValido() {
        assertThatCode(() -> ValidadorAfinidad.validar(Map.of("ingenieria", 80, "salud", 0)))
                .doesNotThrowAnyException();
    }

    @Test
    void rechazaMapaVacioONulo() {
        assertThatThrownBy(() -> ValidadorAfinidad.validar(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ValidadorAfinidad.validar(Map.of())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rechazaValoresFueraDeRango() {
        assertThatThrownBy(() -> ValidadorAfinidad.validar(Map.of("salud", 101)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ValidadorAfinidad.validar(Map.of("salud", -1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rechazaDemasiadasAreas() {
        Map<String, Integer> mapa = new HashMap<>();
        for (int i = 0; i <= ValidadorAfinidad.MAXIMO_AREAS; i++) {
            mapa.put("area" + i, 10);
        }
        assertThatThrownBy(() -> ValidadorAfinidad.validar(mapa)).isInstanceOf(IllegalArgumentException.class);
    }
}