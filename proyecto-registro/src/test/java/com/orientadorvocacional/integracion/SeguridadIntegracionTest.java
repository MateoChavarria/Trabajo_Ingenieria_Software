package com.orientadorvocacional.integracion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integracion de la seguridad: quien eres (token) y que puedes usar (pago).
 * Cubre PI-07 (RNF-04, el historial de otro usuario) y la compuerta de pago
 * de la Sprint 10 en cada ruta.
 */
class SeguridadIntegracionTest extends IntegracionBase {

    // ---------------------------------------------------------------- PI-07
    @Test
    @DisplayName("PI-07: el usuario A solo ve SU historial, aunque pida el de B con ?usuarioId=")
    void usuarioNoPuedeConsultarElHistorialDeOtro() throws Exception {
        darAccesoAUsuarioA();
        acceso.otorgarAccesoCompleto(USUARIO_B);

        // A contesta siempre "ingenieria" y B contesta siempre "salud".
        mvc.perform(conSesion(postJson("/api/test/resultado", respuestasSiempre("ingenieria")), TOKEN_A))
                .andExpect(status().isOk());
        mvc.perform(conSesion(postJson("/api/test/resultado", respuestasSiempre("salud")), TOKEN_B))
                .andExpect(status().isOk());

        // A intenta leer el historial de B pasando su id en la URL: el servidor ignora ese parametro.
        mvc.perform(getConSesion("/api/perfil/historial?usuarioId=" + USUARIO_B, TOKEN_A))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].perfil.areasDominantes[0].codigo", is("ingenieria")));

        // Y B ve el suyo, no el de A.
        mvc.perform(getConSesion("/api/perfil/historial", TOKEN_B))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].perfil.areasDominantes[0].codigo", is("salud")));
    }

    @Test
    @DisplayName("PI-07: sin token, el historial responde 401 (acceso denegado)")
    void historialSinTokenEsDenegado() throws Exception {
        mvc.perform(get("/api/perfil/historial?usuarioId=" + USUARIO_A))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PI-07: con un token que no existe, el historial responde 401")
    void historialConTokenFalsoEsDenegado() throws Exception {
        mvc.perform(getConSesion("/api/perfil/historial", "token-inventado"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("El historial de un usuario nuevo es una lista vacia (200), no un error")
    void historialDeUsuarioNuevoEstaVacio() throws Exception {
        mvc.perform(getConSesion("/api/perfil/historial", TOKEN_A))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("El historial es una funcion gratuita: no exige haber pagado")
    void historialNoExigePago() throws Exception {
        mvc.perform(getConSesion("/api/perfil/historial", TOKEN_A))
                .andExpect(status().isOk());
    }

    // ------------------------------------------- Cabecera Authorization
    @Test
    @DisplayName("Una cabecera Authorization sin el prefijo Bearer se trata como sin sesion (401)")
    void cabeceraSinBearerEsRechazada() throws Exception {
        mvc.perform(get("/api/perfil/historial").header("Authorization", TOKEN_A))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Si el proveedor de autenticacion esta caido, las rutas protegidas responden 503")
    void proveedorCaidoDevuelve503() throws Exception {
        autenticacion.proveedorCaido = true;

        mvc.perform(getConSesion("/api/perfil/historial", TOKEN_A))
                .andExpect(status().isServiceUnavailable());
    }

    // -------------------------------------------- Compuerta de pago
    /** Las 5 rutas que exigen sesion Y pago, como peticiones listas para enviar. */
    private List<MockHttpServletRequestBuilder> rutasDePago() throws Exception {
        Map<String, Object> afinidad = Map.of("afinidadPorArea", Map.of("ingenieria", 50));
        return List.of(
                get("/api/test/preguntas"),
                postJson("/api/test/resultado", respuestasSiempre("ingenieria")),
                postJson("/api/perfil/resumen", afinidad),
                postJson("/api/carreras/recomendadas", afinidad),
                get("/api/carreras/1/simulacion"));
    }

    @Test
    @DisplayName("Sin sesion, las 5 rutas de pago responden 401")
    void rutasDePagoSinSesionResponden401() throws Exception {
        for (MockHttpServletRequestBuilder peticion : rutasDePago()) {
            mvc.perform(peticion).andExpect(status().isUnauthorized());
        }
    }

    @Test
    @DisplayName("Con sesion pero sin haber pagado, las 5 rutas de pago responden 403 con el mensaje de desbloqueo")
    void rutasDePagoSinAccesoResponden403() throws Exception {
        for (MockHttpServletRequestBuilder peticion : rutasDePago()) {
            mvc.perform(conSesion(peticion, TOKEN_A))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(containsString("acceso completo")));
        }
        assertThat(resultados.filas).isEmpty();
    }

    @Test
    @DisplayName("Con acceso completo, las 5 rutas de pago responden 200")
    void rutasDePagoConAccesoResponden200() throws Exception {
        darAccesoAUsuarioA();

        for (MockHttpServletRequestBuilder peticion : rutasDePago()) {
            mvc.perform(conSesion(peticion, TOKEN_A)).andExpect(status().isOk());
        }
    }

    @Test
    @DisplayName("El acceso es por usuario: que A haya pagado no desbloquea a B")
    void elAccesoNoSeComparteEntreUsuarios() throws Exception {
        darAccesoAUsuarioA();

        mvc.perform(getConSesion("/api/test/preguntas", TOKEN_A)).andExpect(status().isOk());
        mvc.perform(getConSesion("/api/test/preguntas", TOKEN_B)).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Las rutas publicas responden 200 sin token")
    void rutasPublicasNoPidenSesion() throws Exception {
        mvc.perform(get("/api/carreras")).andExpect(status().isOk());
        mvc.perform(get("/api/carreras/1")).andExpect(status().isOk());
        mvc.perform(get("/api/carreras/1/pensum")).andExpect(status().isOk());
        mvc.perform(get("/api/universidades")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Las rutas de pagos exigen sesion pero no haber pagado")
    void rutasDePagosSoloExigenSesion() throws Exception {
        mvc.perform(get("/api/pagos/oferta")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/pagos/iniciar")).andExpect(status().isUnauthorized());
        mvc.perform(getConSesion("/api/pagos/oferta", TOKEN_A)).andExpect(status().isOk());
    }
}
