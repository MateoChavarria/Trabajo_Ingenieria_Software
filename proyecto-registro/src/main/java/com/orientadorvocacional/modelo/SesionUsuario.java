package com.orientadorvocacional.modelo;

import java.time.LocalDateTime;

/**
 * Representa la sesion activa de un usuario que ya inicio sesion.
 *
 * Todos los campos son privados. El constructor NO valida nada por
 * si mismo (esa responsabilidad no le corresponde); simplemente
 * delega en el setter correspondiente, igual que en la clase Usuario.
 */
public class SesionUsuario {

    /** Duración de la sesión antes de pedir volver a iniciar sesión. */
    public static final long DURACION_SESION_MINUTOS = 60;

    private String tokenAcceso;
    private String idUsuario;
    private LocalDateTime fechaExpiracion;

    public SesionUsuario(String tokenAcceso, String idUsuario) {
        setTokenAcceso(tokenAcceso);
        setIdUsuario(idUsuario);
        this.fechaExpiracion = LocalDateTime.now().plusMinutes(DURACION_SESION_MINUTOS);
    }

    public String getTokenAcceso() {
        return tokenAcceso;
    }

    public void setTokenAcceso(String tokenAcceso) {
        if (tokenAcceso == null || tokenAcceso.isBlank()) {
            throw new IllegalArgumentException("El token de acceso no puede estar vacío.");
        }
        this.tokenAcceso = tokenAcceso;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        if (idUsuario == null || idUsuario.isBlank()) {
            throw new IllegalArgumentException("El id del usuario no puede estar vacío.");
        }
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    /**
     * Indica si la sesión ya venció y se debe pedir iniciar sesión de nuevo.
     */
    public boolean estaExpirada() {
        return LocalDateTime.now().isAfter(fechaExpiracion);
    }
}