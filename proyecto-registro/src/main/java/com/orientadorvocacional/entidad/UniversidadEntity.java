package com.orientadorvocacional.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Fila de la tabla "universidades" de Supabase. */
@Entity
@Table(name = "universidades")
public class UniversidadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String caracter;

    @Column(nullable = false)
    private String municipio;

    @Column(name = "tiene_informacion_completa", nullable = false)
    private boolean tieneInformacionCompleta;

    protected UniversidadEntity() {
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCaracter() {
        return caracter;
    }

    public String getMunicipio() {
        return municipio;
    }

    public boolean isTieneInformacionCompleta() {
        return tieneInformacionCompleta;
    }
}
