package com.orientadorvocacional.modelo;

/**
 * Representa los posibles estados de una cuenta de usuario dentro del sistema.
 * Se guarda en la columna "estado_cuenta" de la tabla "usuarios" en Supabase.
 */
public enum EstadoCuenta {
    ACTIVO,
    INACTIVO,
    BLOQUEADO;

    /**
     * Convierte el estado al formato exacto (minusculas) que se espera
     * en la columna "estado_cuenta" de la base de datos.
     */
    public String aTextoBaseDeDatos() {
        return this.name().toLowerCase();
    }
}
