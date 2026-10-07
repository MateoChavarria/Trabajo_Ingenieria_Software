package com.orientadorvocacional.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "carreras")
public class CarreraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, columnDefinition = "text")
    private String descripcion;

    @Column(name = "campo_laboral", nullable = false, columnDefinition = "text")
    private String campoLaboral;

    @Column(name = "area_categoria", nullable = false)
    private String areaCategoria;

    @Column(name = "duracion_semestres")
    private Integer duracionSemestres;

    @Column(name = "tarifa_semestre")
    private BigDecimal tarifaSemestre;

    protected CarreraEntity() {
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCampoLaboral() {
        return campoLaboral;
    }

    public String getAreaCategoria() {
        return areaCategoria;
    }

    public Integer getDuracionSemestres() {
        return duracionSemestres;
    }

    public BigDecimal getTarifaSemestre() {
        return tarifaSemestre;
    }
}