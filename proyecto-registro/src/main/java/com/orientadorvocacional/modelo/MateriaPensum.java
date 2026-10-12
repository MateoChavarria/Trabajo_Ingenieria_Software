package com.orientadorvocacional.modelo;

/**
 * Una materia dentro del plan de estudios de una carrera: su nombre,
 * cuántos créditos vale y a qué enfoque temático pertenece.
 */
public class MateriaPensum {

    private String nombre;
    private int creditos;
    private String enfoqueTematico;

    public MateriaPensum(String nombre, int creditos, String enfoqueTematico) {
        setNombre(nombre);
        setCreditos(creditos);
        setEnfoqueTematico(enfoqueTematico);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la materia no puede estar vacío.");
        }
        this.nombre = nombre;
    }

    public int getCreditos() {
        return creditos;
    }

    /** Se permiten 0 créditos: algunas actividades del plan no tienen valor en créditos. */
    public void setCreditos(int creditos) {
        if (creditos < 0) {
            throw new IllegalArgumentException("Los créditos de la materia no pueden ser negativos.");
        }
        this.creditos = creditos;
    }

    public String getEnfoqueTematico() {
        return enfoqueTematico;
    }

    public void setEnfoqueTematico(String enfoqueTematico) {
        if (enfoqueTematico == null || enfoqueTematico.isBlank()) {
            throw new IllegalArgumentException("El enfoque de la materia no puede estar vacío.");
        }
        this.enfoqueTematico = enfoqueTematico;
    }
}
