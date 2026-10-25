package com.orientadorvocacional.integracion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integracion del test vocacional, el perfil y las carreras recomendadas
 * (Historias 3, 5 y 8). Cubre PI-04, PI-05, PI-06 y el lado HTTP de PU-35.
 *
 * El usuario A ya pago (tiene acceso completo) para poder usar estas rutas;
 * el bloqueo por falta de pago se prueba en SeguridadIntegracionTest.
 */
class TestVocacionalIntegracionTest extends IntegracionBase {

    @BeforeEach
    void usuarioAConAcceso() throws Exception {
        darAccesoAUsuarioA();
    }

    // ---------------------------------------------------------------- PI-04
    @Test
    @DisplayName("PI-04: el cuestionario cargado devuelve todas las preguntas con todas sus opciones")
    void cuestionarioDevuelveTodasLasPreguntas() throws Exception {
        mvc.perform(getConSesion("/api/test/preguntas", TOKEN_A))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(catalogo.preguntas.size())))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].opciones", hasSize(3)))
                .andExpect(jsonPath("$[2].opciones", hasSize(3)));
    }

    // ------------------------------------------------- Resultado del test
    @Test
    @DisplayName("Enviar el cuestionario completo calcula la afinidad y la guarda para el usuario del token")
    void cuestionarioCompletoCalculaYGuardaElResultado() throws Exception {
        mvc.perform(conSesion(postJson("/api/test/resultado", respuestasSiempre("ingenieria")), TOKEN_A))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingenieria", is(100)))
                .andExpect(jsonPath("$.salud", is(0)))
                .andExpect(jsonPath("$.administracion", is(0)));

        assertThat(resultados.filas).hasSize(1);
        assertThat(resultados.filas.get(0).getUsuarioId()).isEqualTo(USUARIO_A);
    }

    // ------------------------------------------------------- PU-35 (HTTP)
    @Test
    @DisplayName("PU-35 (HTTP): un cuestionario con una pregunta sin responder se rechaza con 400 y no se guarda")
    void cuestionarioIncompletoEsRechazado() throws Exception {
        Map<String, Object> cuerpo = respuestasSiempre("ingenieria");
        @SuppressWarnings("unchecked")
        List<Object> respuestas = (List<Object>) cuerpo.get("respuestas");
        respuestas.remove(respuestas.size() - 1); // queda sin responder la ultima pregunta

        mvc.perform(conSesion(postJson("/api/test/resultado", cuerpo), TOKEN_A))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("todas las preguntas")));

        assertThat(resultados.filas).isEmpty();
    }

    @Test
    @DisplayName("Una opcion que no pertenece a su pregunta se rechaza con 400")
    void opcionDeOtraPreguntaEsRechazada() throws Exception {
        // La opcion 100 pertenece a la pregunta 1, pero se envia para la pregunta 2.
        Map<String, Object> cuerpo = Map.of("respuestas", List.of(
                Map.of("preguntaId", 1, "opcionId", 100),
                Map.of("preguntaId", 2, "opcionId", 100),
                Map.of("preguntaId", 3, "opcionId", 106)));

        mvc.perform(conSesion(postJson("/api/test/resultado", cuerpo), TOKEN_A))
                .andExpect(status().isBadRequest());

        assertThat(resultados.filas).isEmpty();
    }

    @Test
    @DisplayName("Una lista de respuestas vacia se rechaza con 400")
    void respuestasVaciasSonRechazadas() throws Exception {
        mvc.perform(conSesion(postJsonTexto("/api/test/resultado", "{\"respuestas\":[]}"), TOKEN_A))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("El id del usuario se toma del token, no del cuerpo de la peticion")
    void elResultadoSeGuardaConElUsuarioDelTokenAunqueElCuerpoDigaOtro() throws Exception {
        Map<String, Object> cuerpo = new java.util.HashMap<>(respuestasSiempre("salud"));
        cuerpo.put("usuarioId", USUARIO_B); // intento de suplantacion

        mvc.perform(conSesion(postJson("/api/test/resultado", cuerpo), TOKEN_A))
                .andExpect(status().isOk());

        assertThat(resultados.filas).hasSize(1);
        assertThat(resultados.filas.get(0).getUsuarioId()).isEqualTo(USUARIO_A);
    }

    // ---------------------------------------------------------------- PI-05
    @Test
    @DisplayName("PI-05: las carreras recomendadas salen ordenadas de mayor a menor afinidad")
    void carrerasRecomendadasOrdenadasPorAfinidad() throws Exception {
        Map<String, Object> afinidad = Map.of("afinidadPorArea",
                Map.of("ingenieria", 90, "salud", 60, "administracion", 20));

        mvc.perform(conSesion(postJson("/api/carreras/recomendadas", afinidad), TOKEN_A))
                .andExpect(status().isOk())
                // 4 carreras en el catalogo: 2 de ingenieria (90), 1 de salud (60), 1 de administracion (20)
                .andExpect(jsonPath("$.carreras", hasSize(4)))
                .andExpect(jsonPath("$.carreras[0].afinidad", is(90)))
                .andExpect(jsonPath("$.carreras[1].afinidad", is(90)))
                .andExpect(jsonPath("$.carreras[2].afinidad", is(60)))
                .andExpect(jsonPath("$.carreras[2].carrera.nombre", is("Enfermería")))
                .andExpect(jsonPath("$.carreras[3].afinidad", is(20)))
                .andExpect(jsonPath("$.afinidadBaja", is(false)));
    }

    @Test
    @DisplayName("Si ninguna afinidad llega al 40%, igual se recomiendan carreras y se marca afinidadBaja")
    void afinidadBajaSeMarcaPeroNoOcultaCarreras() throws Exception {
        Map<String, Object> afinidad = Map.of("afinidadPorArea", Map.of("ingenieria", 30, "salud", 20));

        mvc.perform(conSesion(postJson("/api/carreras/recomendadas", afinidad), TOKEN_A))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.afinidadBaja", is(true)))
                .andExpect(jsonPath("$.carreras", hasSize(4)));
    }

    @Test
    @DisplayName("Una afinidad fuera de 0 a 100 se rechaza con 400")
    void afinidadFueraDeRangoEsRechazada() throws Exception {
        Map<String, Object> afinidad = Map.of("afinidadPorArea", Map.of("ingenieria", 150));

        mvc.perform(conSesion(postJson("/api/carreras/recomendadas", afinidad), TOKEN_A))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Una afinidad vacia se rechaza con 400")
    void afinidadVaciaEsRechazada() throws Exception {
        mvc.perform(conSesion(postJsonTexto("/api/carreras/recomendadas", "{\"afinidadPorArea\":{}}"), TOKEN_A))
                .andExpect(status().isBadRequest());
    }

    // ---------------------------------------------------------------- PI-06
    @Test
    @DisplayName("PI-06: el resumen de perfil devuelve las 3 areas dominantes ordenadas, con su interpretacion")
    void resumenDePerfilMuestraLasAreasDominantes() throws Exception {
        Map<String, Object> afinidad = Map.of("afinidadPorArea",
                Map.of("ingenieria", 80, "salud", 60, "administracion", 40, "derecho", 20));

        mvc.perform(conSesion(postJson("/api/perfil/resumen", afinidad), TOKEN_A))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.areasDominantes", hasSize(3)))
                .andExpect(jsonPath("$.areasDominantes[0].codigo", is("ingenieria")))
                .andExpect(jsonPath("$.areasDominantes[0].afinidad", is(80)))
                .andExpect(jsonPath("$.areasDominantes[0].interpretacion").isNotEmpty())
                .andExpect(jsonPath("$.areasDominantes[1].codigo", is("salud")))
                .andExpect(jsonPath("$.areasDominantes[2].codigo", is("administracion")));
    }

    @Test
    @DisplayName("El resumen de perfil rechaza una afinidad no valida con 400")
    void resumenConAfinidadInvalidaEsRechazado() throws Exception {
        Map<String, Object> afinidad = Map.of("afinidadPorArea", Map.of("ingenieria", -5));

        mvc.perform(conSesion(postJson("/api/perfil/resumen", afinidad), TOKEN_A))
                .andExpect(status().isBadRequest());
    }
}
