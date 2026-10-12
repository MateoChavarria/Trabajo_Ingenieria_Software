package com.orientadorvocacional.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Fila de la tabla "materias_carrera" de Supabase. */
@Entity
@Table(name = "materias_carrera")
public class MateriaCarreraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "semestre_id", nullable = false)
    private Integer semestreId;

    @Column(nullable = false, columnDefinition = "text")
    private String nombre;

    @Column(nullable = false)
    private Integer creditos;

    @Column(name = "enfoque_tematico", nullable = false, columnDefinition = "text")
    private String enfoqueTematico;

    protected MateriaCarreraEntity() {
    }

    public Integer getId() {
        return id;
    }

    public Integer getSemestreId() {
        return semestreId;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getCreditos() {
        return creditos;
    }

    public String getEnfoqueTematico() {
        return enfoqueTematico;
    }
}
