package com.orientadorvocacional.integracion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integracion de registro e inicio de sesion (Historias 1 y 2).
 * Cubre PI-01, PI-02 y PI-03 del plan de pruebas.
 */
class AutenticacionIntegracionTest extends IntegracionBase {

    private static final String CORREO = "estudiante@upb.edu.co";
    private static final String CONTRASENA_VALIDA = "Segura123";

    private Map<String, String> credenciales(String correo, String contrasena) {
        return Map.of("correo", correo, "contrasena", contrasena);
    }

    // ---------------------------------------------------------------- PI-01
    @Test
    @DisplayName("PI-01: registrar un correo ya registrado devuelve 409 y no crea un segundo perfil")
    void registrarCorreoYaRegistradoDevuelveConflicto() throws Exception {
        mvc.perform(postJson("/api/auth/registro", credenciales(CORREO, CONTRASENA_VALIDA)))
                .andExpect(status().isOk());

        // Mismo correo otra vez (incluso con otras mayusculas): el proveedor lo rechaza.
        mvc.perform(postJson("/api/auth/registro", credenciales(CORREO.toUpperCase(), CONTRASENA_VALIDA)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("ya está registrado")));

        assertThat(usuarios.guardados).hasSize(1);
    }

    @Test
    @DisplayName("Un registro valido responde 200 y guarda el perfil con el id del proveedor")
    void registroValidoGuardaElPerfil() throws Exception {
        mvc.perform(postJson("/api/auth/registro", credenciales(CORREO, CONTRASENA_VALIDA)))
                .andExpect(status().isOk());

        assertThat(usuarios.guardados).hasSize(1);
        assertThat(usuarios.guardados.keySet().iterator().next()).startsWith("id-nuevo-");
    }

    @Test
    @DisplayName("Un correo con formato invalido se rechaza con 400 sin llegar al proveedor")
    void correoInvalidoDevuelveBadRequest() throws Exception {
        mvc.perform(postJson("/api/auth/registro", credenciales("esto-no-es-un-correo", CONTRASENA_VALIDA)))
                .andExpect(status().isBadRequest());

        assertThat(usuarios.guardados).isEmpty();
    }

    // ---------------------------------------------------------------- PI-02
    @Test
    @DisplayName("PI-02: contrasena sin mayuscula es rechazada por el endpoint")
    void contrasenaSinMayusculaEsRechazada() throws Exception {
        mvc.perform(postJson("/api/auth/registro", credenciales(CORREO, "segura123")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("mayúscula")));

        assertThat(usuarios.guardados).isEmpty();
    }

    @Test
    @DisplayName("PI-02: contrasena sin numero es rechazada por el endpoint")
    void contrasenaSinNumeroEsRechazada() throws Exception {
        mvc.perform(postJson("/api/auth/registro", credenciales(CORREO, "SeguraSegura")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("número")));

        assertThat(usuarios.guardados).isEmpty();
    }

    @Test
    @DisplayName("PI-02: contrasena de menos de 8 caracteres es rechazada")
    void contrasenaCortaEsRechazada() throws Exception {
        mvc.perform(postJson("/api/auth/registro", credenciales(CORREO, "Ab1")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("8 caracteres")));
    }

    // ---------------------------------------------------------------- PI-03
    @Test
    @DisplayName("PI-03: contrasena incorrecta y correo inexistente dan EXACTAMENTE la misma respuesta")
    void credencialesIncorrectasNoRevelanCualDatoFallo() throws Exception {
        mvc.perform(postJson("/api/auth/registro", credenciales(CORREO, CONTRASENA_VALIDA)))
                .andExpect(status().isOk());

        MvcResult contrasenaMala = mvc.perform(
                        postJson("/api/auth/login", credenciales(CORREO, "OtraClave999")))
                .andExpect(status().isUnauthorized())
                .andReturn();
        MvcResult correoInexistente = mvc.perform(
                        postJson("/api/auth/login", credenciales("nadie@upb.edu.co", CONTRASENA_VALIDA)))
                .andExpect(status().isUnauthorized())
                .andReturn();

        // Mismo estado y mismo texto: un atacante no puede saber si el correo existe.
        assertThat(cuerpo(contrasenaMala)).isEqualTo(cuerpo(correoInexistente));
        assertThat(cuerpo(contrasenaMala)).contains("Correo o contraseña incorrectos");
    }

    @Test
    @DisplayName("Un login correcto devuelve el token de sesion y nunca la contrasena")
    void loginCorrectoDevuelveSesion() throws Exception {
        mvc.perform(postJson("/api/auth/registro", credenciales(CORREO, CONTRASENA_VALIDA)))
                .andExpect(status().isOk());

        mvc.perform(postJson("/api/auth/login", credenciales(CORREO, CONTRASENA_VALIDA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenAcceso").isNotEmpty())
                .andExpect(jsonPath("$.idUsuario").isNotEmpty())
                .andExpect(content().string(not(containsString(CONTRASENA_VALIDA))));
    }

    @Test
    @DisplayName("Login con campos vacios responde 401 sin consultar el proveedor")
    void loginConCamposVaciosDevuelveNoAutorizado() throws Exception {
        mvc.perform(postJson("/api/auth/login", credenciales("", "")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Si el proveedor de autenticacion esta caido, el login responde 500 con mensaje controlado")
    void loginConProveedorCaidoDevuelveErrorControlado() throws Exception {
        autenticacion.proveedorCaido = true;

        mvc.perform(postJson("/api/auth/login", credenciales(CORREO, CONTRASENA_VALIDA)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Un JSON mal formado se responde 400 con mensaje generico (sin detalles internos)")
    void jsonMalFormadoDevuelveBadRequest() throws Exception {
        mvc.perform(postJsonTexto("/api/auth/login", "{esto no es json"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(not(containsString("Exception"))));
    }
}
