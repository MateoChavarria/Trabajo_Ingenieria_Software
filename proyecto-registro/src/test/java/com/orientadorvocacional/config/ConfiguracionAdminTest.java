package com.orientadorvocacional.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConfiguracionAdminTest {

    @Test
    void reconoceElCorreoExactoSinImportarMayusculasNiEspacios() {
        ConfiguracionAdmin configuracion = new ConfiguracionAdmin(" Admin@Gmail.com ");

        assertThat(configuracion.esAdministrador("admin@gmail.com")).isTrue();
        assertThat(configuracion.esAdministrador("  ADMIN@GMAIL.COM")).isTrue();
    }

    @Test
    void otrosCorreosNoSonAdministradores() {
        ConfiguracionAdmin configuracion = new ConfiguracionAdmin("admin@gmail.com");

        assertThat(configuracion.esAdministrador("otro@gmail.com")).isFalse();
        assertThat(configuracion.esAdministrador("admin+1@gmail.com")).isFalse();
        assertThat(configuracion.esAdministrador("admin@gmail.com.co")).isFalse();
        assertThat(configuracion.esAdministrador(null)).isFalse();
    }

    @Test
    void sinCorreoConfiguradoNadieEsAdministrador() {
        assertThat(new ConfiguracionAdmin("").esAdministrador("")).isFalse();
        assertThat(new ConfiguracionAdmin(null).esAdministrador("admin@gmail.com")).isFalse();
    }
}
