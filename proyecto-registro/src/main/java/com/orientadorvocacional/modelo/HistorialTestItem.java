package com.orientadorvocacional.modelo;

import java.time.LocalDateTime;

public class HistorialTestItem {

    private final LocalDateTime fecha;
    private final PerfilVocacional perfil;

    public HistorialTestItem(LocalDateTime fecha, PerfilVocacional perfil) {
        this.fecha = fecha;
        this.perfil = perfil;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public PerfilVocacional getPerfil() {
        return perfil;
    }
}