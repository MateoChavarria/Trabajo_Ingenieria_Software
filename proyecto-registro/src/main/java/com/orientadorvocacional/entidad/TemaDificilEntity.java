package com.orientadorvocacional.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Fila de la tabla "temas_dificiles_materia" de Supabase. */
@Entity
@Table(name = "temas_dificiles_materia")
public class TemaDificilEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "materia_id", nullable = false)
    private Integer materiaId;

    /** Posición del tema dentro de la materia: 1, 2 o 3. */
    @Column(nullable = false)
    private Integer orden;

    @Column(nullable = false, columnDefinition = "text")
    private String tema;

    protected TemaDificilEntity() {
    }

    public Integer getId() {
        return id;
    }

    public Integer getMateriaId() {
        return materiaId;
    }

    public Integer getOrden() {
        return orden;
    }

    public String getTema() {
        return tema;
    }
}
