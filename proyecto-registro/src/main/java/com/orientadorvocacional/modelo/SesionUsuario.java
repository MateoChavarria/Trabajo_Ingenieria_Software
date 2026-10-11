package com.orientadorvocacional.modelo;

import java.time.Instant;

/** Sesion activa: token de Supabase e instante (UTC) en que deja de ser valido. */
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

    public String getTokenAcceso() { return tokenAcceso; }

    public void setTokenAcceso(String tokenAcceso) {
        // conserva aqui tus validaciones actuales del setter
        this.tokenAcceso = tokenAcceso;
    }

    public String getIdUsuario() { return idUsuario; }

    public void setIdUsuario(String idUsuario) {
        // conserva aqui tus validaciones actuales del setter
        this.idUsuario = idUsuario;
    }

    public Instant getFechaExpiracion() { return fechaExpiracion; }

    public boolean estaExpirada() {
        return Instant.now().isAfter(fechaExpiracion);
    }
}