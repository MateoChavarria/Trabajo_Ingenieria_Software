package com.orientadorvocacional.integracion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integracion de la pasarela de pago simulada (Sprint 10): oferta, iniciar,
 * procesar con tarjetas de prueba, cancelar, consultar y las reglas de
 * seguridad (pagos ajenos, referencias invalidas, pagos repetidos).
 */
class PagosIntegracionTest extends IntegracionBase {

    private static final String PATRON_REFERENCIA = "^PAY-[0-9A-F]{12}$";
    private static final String TARJETA_OK = "4242424242424242";
    private static final String TARJETA_SIN_FONDOS = "4000000000000002";

    private String vencimientoFuturo() {
        return YearMonth.now().plusYears(2).format(DateTimeFormatter.ofPattern("MM/yy"));
    }

    private Map<String, String> tarjeta(String numero) {
        return Map.of("nombreTitular", "Ana Perez", "numeroTarjeta", numero,
                "vencimiento", vencimientoFuturo(), "cvv", "123");
    }

    /** Inicia un pago como el usuario A y devuelve su referencia. */
    private String iniciarPagoDeA() throws Exception {
        String json = cuerpo(mvc.perform(conSesion(post("/api/pagos/iniciar"), TOKEN_A))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referencia", matchesPattern(PATRON_REFERENCIA)))
                .andExpect(jsonPath("$.estado", is("pendiente")))
                .andReturn());
        return JSON.readTree(json).get("referencia").asText();
    }

    private void pagar(String referencia, String numeroTarjeta, String token) throws Exception {
        mvc.perform(conSesion(postJson("/api/pagos/" + referencia + "/procesar", tarjeta(numeroTarjeta)), token));
    }

    // ------------------------------------------------------------ Oferta
    @Test
    @DisplayName("La oferta muestra el precio de 20.000 COP y que aun no hay acceso")
    void ofertaSinAcceso() throws Exception {
        mvc.perform(getConSesion("/api/pagos/oferta", TOKEN_A))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monto", is(20000)))
                .andExpect(jsonPath("$.moneda", is("COP")))
                .andExpect(jsonPath("$.accesoCompleto", is(false)));
    }

    // ------------------------------------------------------ Pago exitoso
    @Test
    @DisplayName("Flujo feliz: iniciar, pagar con 4242... y quedar con acceso a las funciones de pago")
    void pagoExitosoDesbloqueaLasFunciones() throws Exception {
        mvc.perform(getConSesion("/api/test/preguntas", TOKEN_A)).andExpect(status().isForbidden());

        String referencia = iniciarPagoDeA();
        mvc.perform(conSesion(postJson("/api/pagos/" + referencia + "/procesar", tarjeta(TARJETA_OK)), TOKEN_A))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("exitoso")))
                // Los datos de la tarjeta nunca vuelven en la respuesta.
                .andExpect(content().string(not(containsString(TARJETA_OK))));

        mvc.perform(getConSesion("/api/pagos/oferta", TOKEN_A))
                .andExpect(jsonPath("$.accesoCompleto", is(true)));
        mvc.perform(getConSesion("/api/test/preguntas", TOKEN_A)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Iniciar dos veces seguidas reutiliza el mismo pago pendiente")
    void iniciarDosVecesReutilizaElPendiente() throws Exception {
        String primera = iniciarPagoDeA();
        String segunda = iniciarPagoDeA();

        assertThat(segunda).isEqualTo(primera);
        assertThat(pagos.total()).isEqualTo(1);
    }

    @Test
    @DisplayName("Quien ya tiene acceso completo no puede iniciar otro pago (409)")
    void iniciarConAccesoDevuelveConflicto() throws Exception {
        darAccesoAUsuarioA();

        mvc.perform(conSesion(post("/api/pagos/iniciar"), TOKEN_A))
                .andExpect(status().isConflict());
    }

    // ------------------------------------------------------ Pago fallido
    @Test
    @DisplayName("Tarjeta sin fondos: responde 200 con estado fallido y NO da acceso; luego puede reintentar")
    void pagoFallidoNoDaAccesoYPermiteReintentar() throws Exception {
        String referencia = iniciarPagoDeA();

        mvc.perform(conSesion(postJson("/api/pagos/" + referencia + "/procesar", tarjeta(TARJETA_SIN_FONDOS)), TOKEN_A))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("fallido")))
                .andExpect(jsonPath("$.motivoFallo", is("Fondos insuficientes.")));

        mvc.perform(getConSesion("/api/test/preguntas", TOKEN_A)).andExpect(status().isForbidden());

        // Reintento: se crea un pago nuevo (el anterior ya quedo resuelto) y esta vez se aprueba.
        String nueva = iniciarPagoDeA();
        assertThat(nueva).isNotEqualTo(referencia);
        pagar(nueva, TARJETA_OK, TOKEN_A);
        mvc.perform(getConSesion("/api/test/preguntas", TOKEN_A)).andExpect(status().isOk());
    }

    // ---------------------------------------- Datos de tarjeta invalidos
    @Test
    @DisplayName("Un numero de tarjeta con formato invalido responde 400 y el pago sigue pendiente")
    void tarjetaConFormatoInvalidoDevuelve400() throws Exception {
        String referencia = iniciarPagoDeA();

        mvc.perform(conSesion(postJson("/api/pagos/" + referencia + "/procesar", tarjeta("1234")), TOKEN_A))
                .andExpect(status().isBadRequest());

        mvc.perform(getConSesion("/api/pagos/" + referencia, TOKEN_A))
                .andExpect(jsonPath("$.estado", is("pendiente")));
    }

    @Test
    @DisplayName("Una tarjeta vencida responde 400")
    void tarjetaVencidaDevuelve400() throws Exception {
        String referencia = iniciarPagoDeA();
        Map<String, String> vencida = Map.of("nombreTitular", "Ana Perez", "numeroTarjeta", TARJETA_OK,
                "vencimiento", "01/20", "cvv", "123");

        mvc.perform(conSesion(postJson("/api/pagos/" + referencia + "/procesar", vencida), TOKEN_A))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------ Referencias y duenos
    @Test
    @DisplayName("Una referencia mal formada responde 400 en consultar, procesar y cancelar")
    void referenciaMalFormadaDevuelve400() throws Exception {
        mvc.perform(getConSesion("/api/pagos/abc", TOKEN_A)).andExpect(status().isBadRequest());
        mvc.perform(conSesion(post("/api/pagos/abc/cancelar"), TOKEN_A)).andExpect(status().isBadRequest());
        mvc.perform(conSesion(postJson("/api/pagos/abc/procesar", tarjeta(TARJETA_OK)), TOKEN_A))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Una referencia bien formada pero inexistente responde 404")
    void referenciaInexistenteDevuelve404() throws Exception {
        mvc.perform(getConSesion("/api/pagos/PAY-ABCDEF123456", TOKEN_A)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("El usuario B no puede ver, pagar ni cancelar el pago del usuario A (404)")
    void pagoAjenoResponde404() throws Exception {
        String referencia = iniciarPagoDeA();

        mvc.perform(getConSesion("/api/pagos/" + referencia, TOKEN_B)).andExpect(status().isNotFound());
        mvc.perform(conSesion(post("/api/pagos/" + referencia + "/cancelar"), TOKEN_B)).andExpect(status().isNotFound());
        mvc.perform(conSesion(postJson("/api/pagos/" + referencia + "/procesar", tarjeta(TARJETA_OK)), TOKEN_B))
                .andExpect(status().isNotFound());

        // B no obtuvo acceso con el pago de A.
        mvc.perform(getConSesion("/api/test/preguntas", TOKEN_B)).andExpect(status().isForbidden());
    }

    // ------------------------------------------------- Pagos repetidos
    @Test
    @DisplayName("Procesar dos veces el mismo pago responde 409 la segunda vez")
    void procesarDosVecesDevuelveConflicto() throws Exception {
        String referencia = iniciarPagoDeA();
        pagar(referencia, TARJETA_OK, TOKEN_A);

        mvc.perform(conSesion(postJson("/api/pagos/" + referencia + "/procesar", tarjeta(TARJETA_OK)), TOKEN_A))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Cancelar deja el pago cancelado y despues ya no se puede procesar (409)")
    void cancelarYLuegoProcesarDevuelveConflicto() throws Exception {
        String referencia = iniciarPagoDeA();

        mvc.perform(conSesion(post("/api/pagos/" + referencia + "/cancelar"), TOKEN_A))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("cancelado")));

        mvc.perform(conSesion(postJson("/api/pagos/" + referencia + "/procesar", tarjeta(TARJETA_OK)), TOKEN_A))
                .andExpect(status().isConflict());
        mvc.perform(getConSesion("/api/test/preguntas", TOKEN_A)).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("No se puede cancelar un pago que ya fue aprobado (409)")
    void cancelarPagoAprobadoDevuelveConflicto() throws Exception {
        String referencia = iniciarPagoDeA();
        pagar(referencia, TARJETA_OK, TOKEN_A);

        mvc.perform(conSesion(post("/api/pagos/" + referencia + "/cancelar"), TOKEN_A))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Sin sesion, ninguna ruta de pagos responde (401)")
    void pagosSinSesionDevuelve401() throws Exception {
        mvc.perform(get("/api/pagos/PAY-ABCDEF123456")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/pagos/PAY-ABCDEF123456/cancelar")).andExpect(status().isUnauthorized());
    }
}
