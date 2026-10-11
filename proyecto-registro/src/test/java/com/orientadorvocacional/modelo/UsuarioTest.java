package com.orientadorvocacional.modelo;

import com.orientadorvocacional.excepciones.ContrasenaInvalidaException;
import com.orientadorvocacional.excepciones.CorreoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioTest {

    @Test
    void aceptaCorreosConSubdominios() throws Exception {
        Usuario usuario = new Usuario("Estudiante@Upb.edu.co", "Segura123");
        assertThat(usuario.getCorreo()).isEqualTo("estudiante@upb.edu.co");
    }

    @Test
    void rechazaCorreosMalFormados() {
        assertThatThrownBy(() -> new Usuario("a@b", "Segura123"))
                .isInstanceOf(CorreoInvalidoException.class);
        assertThatThrownBy(() -> new Usuario("<script>@x.com", "Segura123"))
                .isInstanceOf(CorreoInvalidoException.class);
    }

    @Test
    void rechazaCorreosDemasiadoLargos() {
        String correo = "a".repeat(250) + "@x.com";
        assertThatThrownBy(() -> new Usuario(correo, "Segura123"))
                .isInstanceOf(CorreoInvalidoException.class);
    }

    @Test
    void rechazaContrasenasDebilesODemasiadoLargas() {
        assertThatThrownBy(() -> new Usuario("a@x.com", "corta1A"))
                .isInstanceOf(ContrasenaInvalidaException.class);
        assertThatThrownBy(() -> new Usuario("a@x.com", "sinmayuscula1"))
                .isInstanceOf(ContrasenaInvalidaException.class);
        assertThatThrownBy(() -> new Usuario("a@x.com", "A1" + "x".repeat(80)))
                .isInstanceOf(ContrasenaInvalidaException.class);
    }
}