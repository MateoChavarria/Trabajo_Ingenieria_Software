package com.orientadorvocacional.modelo;

import java.math.BigDecimal;

/**
 * Representa una carrera de pregrado. Mismo patron de encapsulamiento
 * del resto del proyecto: constructor delega en los setters, y estos
 * validan.
 */
public class Carrera {

    private int id;
    private String nombre;
    private String descripcion;
    private String campoLaboral;
    private String areaCategoria;
    private Integer duracionSemestres;
    private BigDecimal tarifaSemestre;

    public Carrera(int id, String nombre, String descripcion, String campoLaboral,
                    String areaCategoria, Integer duracionSemestres, BigDecimal tarifaSemestre) {
        setId(id);
        setNombre(nombre);
        setDescripcion(descripcion);
        setCampoLaboral(campoLaboral);
        setAreaCategoria(areaCategoria);
        this.duracionSemestres = duracionSemestres;
        this.tarifaSemestre = tarifaSemestre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El id de la carrera debe ser positivo.");
        }
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la carrera no puede estar vacío.");
        }
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía.");
        }
        this.descripcion = descripcion;
    }

    public String getCampoLaboral() {
        return campoLaboral;
    }

    public void setCampoLaboral(String campoLaboral) {
        if (campoLaboral == null || campoLaboral.isBlank()) {
            throw new IllegalArgumentException("El campo laboral no puede estar vacío.");
        }
        this.campoLaboral = campoLaboral;
    }

    public String getAreaCategoria() {
        return areaCategoria;
    }

    public void setAreaCategoria(String areaCategoria) {
        if (areaCategoria == null || areaCategoria.isBlank()) {
            throw new IllegalArgumentException("El área de la carrera no puede estar vacía.");
        }
        this.areaCategoria = areaCategoria;
    }

    public Integer getDuracionSemestres() {
        return duracionSemestres;
    }

    public BigDecimal getTarifaSemestre() {
        return tarifaSemestre;
    }
}