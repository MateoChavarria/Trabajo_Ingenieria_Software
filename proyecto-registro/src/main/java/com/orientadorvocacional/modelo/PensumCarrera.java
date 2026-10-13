package com.orientadorvocacional.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * El plan de estudios completo de una carrera: sus semestres en orden,
 * cada uno con sus materias.
 */
public class PensumCarrera {

    private int carreraId;
    private String nombreCarrera;
    private List<SemestrePensum> semestres;

    public PensumCarrera(int carreraId, String nombreCarrera, List<SemestrePensum> semestres) {
        setCarreraId(carreraId);
        setNombreCarrera(nombreCarrera);
        setSemestres(semestres);
    }

    public int getCarreraId() {
        return carreraId;
    }

    public void setCarreraId(int carreraId) {
        if (carreraId <= 0) {
            throw new IllegalArgumentException("El id de la carrera debe ser positivo.");
        }
        this.carreraId = carreraId;
    }

    public String getNombreCarrera() {
        return nombreCarrera;
    }

    public void setNombreCarrera(String nombreCarrera) {
        if (nombreCarrera == null || nombreCarrera.isBlank()) {
            throw new IllegalArgumentException("El nombre de la carrera no puede estar vacío.");
        }
        this.nombreCarrera = nombreCarrera;
    }

    public List<SemestrePensum> getSemestres() {
        return semestres;
    }

    /** Guarda una copia de solo lectura, para que nadie la modifique desde fuera. */
    public void setSemestres(List<SemestrePensum> semestres) {
        if (semestres == null) {
            throw new IllegalArgumentException("La lista de semestres no puede ser nula.");
        }
        this.semestres = Collections.unmodifiableList(new ArrayList<>(semestres));
    }

    /** Suma de los créditos de todos los semestres. */
    public int getCreditosTotales() {
        int total = 0;
        for (SemestrePensum semestre : semestres) {
            total += semestre.getCreditosTotales();
        }
        return total;
    }
}
