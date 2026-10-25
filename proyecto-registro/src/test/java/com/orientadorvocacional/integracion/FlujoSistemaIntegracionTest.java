package com.orientadorvocacional.integracion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de SISTEMA a nivel de API: recorren el flujo completo de punta a
 * punta como lo haria el frontend, pero sin navegador. Cubren la parte
 * automatizable de PS-01, PS-02 y PS-03.
 *
 * Lo que NO se automatiza aqui (y queda en la guia manual E2E): los clics en
 * pantalla (por ejemplo "clic en una materia abre su detalle") y el tiempo
 * real con Supabase y la red. Ver el documento de pruebas.
 */
class FlujoSistemaIntegracionTest extends IntegracionBase {

    private static final String CORREO = "nuevo@upb.edu.co";
    private static final String CONTRASENA = "Segura123";

    // ---------------------------------------------------------------- PS-01
    @Test
    @DisplayName("PS-01: registro, login, pago, test y recomendaciones sin errores")
    void flujoCompletoDeRegistroATestYRecomendaciones() throws Exception {
        // 1) Registro y login
        Map<String, String> credenciales = Map.of("correo", CORREO, "contrasena", CONTRASENA);
        mvc.perform(postJson("/api/auth/registro", credenciales)).andExpect(status().isOk());
        String token = JSON.readTree(cuerpo(mvc.perform(postJson("/api/auth/login", credenciales))
                .andExpect(status().isOk()).andReturn())).get("tokenAcceso").asText();

        // 2) Sin pagar, el test esta bloqueado
        mvc.perform(getConSesion("/api/test/preguntas", token)).andExpect(status().isForbidden());

        // 3) Pago con la tarjeta de prueba
        String vencimiento = YearMonth.now().plusYears(2).format(DateTimeFormatter.ofPattern("MM/yy"));
        String referencia = JSON.readTree(cuerpo(mvc.perform(conSesion(post("/api/pagos/iniciar"), token))
                .andExpect(status().isOk()).andReturn())).get("referencia").asText();
        mvc.perform(conSesion(postJson("/api/pagos/" + referencia + "/procesar", Map.of(
                        "nombreTitular", "Ana Perez", "numeroTarjeta", "4242424242424242",
                        "vencimiento", vencimiento, "cvv", "123")), token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("exitoso")));

        // 4) Test completo
        mvc.perform(getConSesion("/api/test/preguntas", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(catalogo.preguntas.size())));
        String resultado = cuerpo(mvc.perform(conSesion(
                        postJson("/api/test/resultado", respuestasSiempre("ingenieria")), token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingenieria", is(100)))
                .andReturn());

        // 5) Perfil y recomendaciones con el resultado recibido
        Map<String, Object> afinidad = Map.of("afinidadPorArea", JSON.readValue(resultado, Map.class));
        mvc.perform(conSesion(postJson("/api/perfil/resumen", afinidad), token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.areasDominantes[0].codigo", is("ingenieria")));
        mvc.perform(conSesion(postJson("/api/carreras/recomendadas", afinidad), token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carreras[0].afinidad", is(100)))
                .andExpect(jsonPath("$.carreras[0].carrera.nombre", is("Ingeniería de Sistemas")));

        // 6) Aparece en su historial
        mvc.perform(getConSesion("/api/perfil/historial", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    // ---------------------------------------------------------------- PS-02
    @Test
    @DisplayName("PS-02: las carreras recomendadas se calculan en menos de 10 segundos")
    void recomendacionesTardanMenosDeDiezSegundos() throws Exception {
        darAccesoAUsuarioA();
        Map<String, Object> afinidad = Map.of("afinidadPorArea",
                Map.of("ingenieria", 90, "salud", 60, "administracion", 20));

        long inicio = System.nanoTime();
        mvc.perform(conSesion(postJson("/api/carreras/recomendadas", afinidad), TOKEN_A))
                .andExpect(status().isOk());
        Duration duracion = Duration.ofNanos(System.nanoTime() - inicio);

        // Mide la capa de aplicacion (sin base de datos real ni red). La medicion contra Supabase
        // se hace a mano con la guia de pruebas.
        assertThat(duracion).isLessThan(Duration.ofSeconds(10));
    }

    // ---------------------------------------------------------------- PS-03
    @Test
    @DisplayName("PS-03 (API): universidades, pensum por semestres y materias, sin iniciar sesion")
    void exploracionPublicaDeUniversidadesYPensum() throws Exception {
        mvc.perform(get("/api/universidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].nombre", hasItem("Universidad Pontificia Bolivariana")));

        mvc.perform(get("/api/carreras/1/pensum"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.semestres", hasSize(2)))
                .andExpect(jsonPath("$.semestres[0].materias", hasSize(2)))
                .andExpect(jsonPath("$.semestres[0].materias[0].creditos", greaterThan(0)))
                .andExpect(jsonPath("$.semestres[0].materias[0].enfoqueTematico").isNotEmpty());
    }
}
