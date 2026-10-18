package com.orientadorvocacional.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * La simulación de una carrera: todo lo que la vista necesita para
 * recorrerla semestre por semestre (materias, enfoque y temas difíciles)
 * y para mostrar el resumen general al llegar al último semestre.
 *
 * Mismo patrón del proyecto: el constructor NO valida, delega en los
 * setters, y los setters son los que validan.
 */
public class SimulacionCarrera {

    /** Texto que la vista debe mostrar siempre junto a la simulación. */
    public static final String AVISO =
            "Esta es una simulación referencial y no representa el plan académico oficial vigente.";

    private int carreraId;
    private String nombreCarrera;
    private List<SemestreSimulacion> semestres;

    public SimulacionCarrera(int carreraId, String nombreCarrera, List<SemestreSimulacion> semestres) {
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

    /** Los semestres en orden (1, 2, 3...). */
    public List<SemestreSimulacion> getSemestres() {
        return semestres;
    }

    /** Sin semestres no hay nada que simular. Guarda una copia de solo lectura. */
    public void setSemestres(List<SemestreSimulacion> semestres) {
        if (semestres == null || semestres.isEmpty()) {
            throw new IllegalArgumentException("La simulación debe tener al menos un semestre.");
        }
        this.semestres = Collections.unmodifiableList(new ArrayList<>(semestres));
    }

    /** Duración total de la carrera, en semestres. */
    public int getTotalSemestres() {
        return semestres.size();
    }

    /** Cantidad de materias de toda la carrera. */
    public int getTotalMaterias() {
        int total = 0;
        for (SemestreSimulacion semestre : semestres) {
            total += semestre.getMaterias().size();
        }
        return total;
    }

    public int getCreditosTotales() {
        int total = 0;
        for (SemestreSimulacion semestre : semestres) {
            total += semestre.getCreditosTotales();
        }
        return total;
    }

    public String getAviso() {
        return AVISO;
    }
}
