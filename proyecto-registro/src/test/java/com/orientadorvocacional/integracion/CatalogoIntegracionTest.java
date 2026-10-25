package com.orientadorvocacional.integracion;

import com.orientadorvocacional.repositorio.jpa.UniversidadJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integracion de la parte publica: universidades, carreras, pensum y
 * simulacion (Historias 4 y 6). Cubre PI-08 y PI-09.
 */
class CatalogoIntegracionTest extends IntegracionBase {

    // ---------------------------------------------------------------- PI-08
    @Test
    @DisplayName("PI-08: GET /api/universidades devuelve la lista ordenada por nombre")
    void universidadesSalenOrdenadasPorNombre() throws Exception {
        mvc.perform(get("/api/universidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].nombre", contains(
                        "Universidad de Antioquia",
                        "Universidad EAFIT",
                        "Universidad Pontificia Bolivariana")));
    }

    @Test
    @DisplayName("PI-08: la consulta real a la base de datos esta declarada con orden alfabetico")
    void laConsultaRealOrdenaPorNombre() throws Exception {
        // En esta prueba la base es falsa, asi que el orden REAL lo garantiza el nombre del metodo
        // de Spring Data (findAllByOrderByNombreAsc). Si alguien lo cambia, esta prueba falla.
        assertThat(UniversidadJpaRepository.class.getMethod("findAllByOrderByNombreAsc")).isNotNull();
    }

    @Test
    @DisplayName("Si falla la consulta de universidades, responde 500 con mensaje controlado")
    void fallaEnUniversidadesDevuelve500() throws Exception {
        catalogo.universidadesCaidas = true;

        mvc.perform(get("/api/universidades"))
                .andExpect(status().isInternalServerError());
    }

    // ---------------------------------------------------------------- PI-09
    @Test
    @DisplayName("PI-09: el pensum de una carrera de la UPB responde 200 con sus semestres en orden")
    void pensumDeCarreraUpbResponde200() throws Exception {
        mvc.perform(get("/api/carreras/1/pensum"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carreraId", is(1)))
                .andExpect(jsonPath("$.semestres", hasSize(2)))
                .andExpect(jsonPath("$.semestres[0].numero", is(1)))
                .andExpect(jsonPath("$.semestres[1].numero", is(2)))
                .andExpect(jsonPath("$.semestres[0].materias[0].nombre", is("Cálculo Diferencial")))
                .andExpect(jsonPath("$.creditosTotales", greaterThan(0)));
    }

    @Test
    @DisplayName("PI-09: el pensum de una carrera de otra universidad responde 404")
    void pensumDeOtraUniversidadResponde404() throws Exception {
        mvc.perform(get("/api/carreras/3/pensum"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PI-09: el pensum de una carrera inexistente responde 404")
    void pensumDeCarreraInexistenteResponde404() throws Exception {
        mvc.perform(get("/api/carreras/999/pensum"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PI-09: un id menor o igual a 0 responde 400")
    void pensumConIdNoPositivoResponde400() throws Exception {
        mvc.perform(get("/api/carreras/0/pensum")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/carreras/-7/pensum")).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Un id que no es numero responde 400, no 500")
    void pensumConIdNoNumericoResponde400() throws Exception {
        mvc.perform(get("/api/carreras/abc/pensum")).andExpect(status().isBadRequest());
    }

    // ------------------------------------------------ Carreras (gratis)
    @Test
    @DisplayName("GET /api/carreras lista todas las carreras sin pedir sesion")
    void listarCarrerasEsPublico() throws Exception {
        mvc.perform(get("/api/carreras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    @DisplayName("El detalle de una carrera existente responde 200 y el de una inexistente 404")
    void detalleDeCarrera() throws Exception {
        mvc.perform(get("/api/carreras/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Enfermería")));
        mvc.perform(get("/api/carreras/999")).andExpect(status().isNotFound());
        mvc.perform(get("/api/carreras/0")).andExpect(status().isBadRequest());
    }

    // --------------------------------------------------- Simulacion
    @Test
    @DisplayName("La simulacion (con acceso completo) trae los temas dificiles de cada materia")
    void simulacionTraeTemasDificiles() throws Exception {
        darAccesoAUsuarioA();

        mvc.perform(getConSesion("/api/carreras/1/simulacion", TOKEN_A))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.semestres", hasSize(2)))
                .andExpect(jsonPath("$.semestres[0].materias[0].temasDificiles", contains("Límites", "Derivadas")))
                .andExpect(jsonPath("$.semestres[1].materias[0].temasDificiles", hasSize(0)));
    }

    @Test
    @DisplayName("La simulacion de una carrera de otra universidad responde 404 aun con acceso completo")
    void simulacionDeOtraUniversidadResponde404() throws Exception {
        darAccesoAUsuarioA();

        mvc.perform(getConSesion("/api/carreras/3/simulacion", TOKEN_A))
                .andExpect(status().isNotFound());
    }
}
