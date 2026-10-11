package com.orientadorvocacional.modelo;

import java.time.Instant;

/**
 * Sesion activa de un usuario: el token emitido por Supabase y el
 * instante (UTC) en que deja de ser valido. Se usa Instant para que el
 * navegador interprete la fecha sin ambiguedad de zona horaria.
 */
public class SesionUsuario {

    public static final long DURACION_SESION_POR_DEFECTO_SEGUNDOS = 3600;

    private String tokenAcceso;
    private String idUsuario;
    private final Instant fechaExpiracion;

    public SesionUsuario(String tokenAcceso, String idUsuario) {
        this(tokenAcceso, idUsuario, DURACION_SESION_POR_DEFECTO_SEGUNDOS);
    }

    public SesionUsuario(String tokenAcceso, String idUsuario, long segundosValidez) {
        setTokenAcceso(tokenAcceso);
        setIdUsuario(idUsuario);
        long validez = segundosValidez > 0 ? segundosValidez : DURACION_SESION_POR_DEFECTO_SEGUNDOS;
        this.fechaExpiracion = Instant.now().plusSeconds(validez);
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

    public Instant getFechaExpiracion() {
        return fechaExpiracion;
    }

    public boolean estaExpirada() {
        return Instant.now().isAfter(fechaExpiracion);
    }
}