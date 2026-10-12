package com.orientadorvocacional.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Fila de la tabla "semestres_carrera" de Supabase. */
@Entity
@Table(name = "semestres_carrera")
public class SemestreCarreraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "carrera_id", nullable = false)
    private Integer carreraId;

    @Column(nullable = false)
    private Integer numero;

    @Column(name = "enfoque_principal", nullable = false, columnDefinition = "text")
    private String enfoquePrincipal;

    @Column(nullable = false, columnDefinition = "text")
    private String descripcion;

    protected SemestreCarreraEntity() {
    }

    public Integer getId() {
        return id;
    }

    public Integer getCarreraId() {
        return carreraId;
    }

    public Integer getNumero() {
        return numero;
    }

    public String getEnfoquePrincipal() {
        return enfoquePrincipal;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
