package com.orientadorvocacional.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Correo de la cuenta administradora. Se define en application.properties
 * (que no se sube a GitHub) con la propiedad app.admin.correo. Si no se define,
 * nadie es administrador y el panel queda cerrado para todos.
 */
@Component
public class ConfiguracionAdmin {

    private final String correoAdministrador;

    public ConfiguracionAdmin(@Value("${app.admin.correo:}") String correoAdministrador) {
        this.correoAdministrador = correoAdministrador == null
                ? "" : correoAdministrador.trim().toLowerCase(Locale.ROOT);
    }

    /** Comparacion exacta (sin distinguir mayusculas): "a@gmail.com" no es "a+1@gmail.com". */
    public boolean esAdministrador(String correo) {
        return !correoAdministrador.isEmpty()
                && correo != null
                && correoAdministrador.equals(correo.trim().toLowerCase(Locale.ROOT));
    }
}
