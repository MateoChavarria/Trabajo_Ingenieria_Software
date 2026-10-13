package com.orientadorvocacional.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Un semestre del plan de estudios: su número, el enfoque que lo
 * caracteriza y la lista de materias que se ven en él.
 */
public class SemestrePensum {

    private int numero;
    private String enfoquePrincipal;
    private String descripcion;
    private List<MateriaPensum> materias;

    public SemestrePensum(int numero, String enfoquePrincipal, String descripcion,
                          List<MateriaPensum> materias) {
        setNumero(numero);
        setEnfoquePrincipal(enfoquePrincipal);
        setDescripcion(descripcion);
        setMaterias(materias);
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        if (numero <= 0) {
            throw new IllegalArgumentException("El número de semestre debe ser positivo.");
        }
        this.numero = numero;
    }

    public String getEnfoquePrincipal() {
        return enfoquePrincipal;
    }

    public void setEnfoquePrincipal(String enfoquePrincipal) {
        if (enfoquePrincipal == null || enfoquePrincipal.isBlank()) {
            throw new IllegalArgumentException("El enfoque del semestre no puede estar vacío.");
        }
        this.enfoquePrincipal = enfoquePrincipal;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción del semestre no puede estar vacía.");
        }
        this.descripcion = descripcion;
    }

    public List<MateriaPensum> getMaterias() {
        return materias;
    }

    /** Guarda una copia de solo lectura, para que nadie la modifique desde fuera. */
    public void setMaterias(List<MateriaPensum> materias) {
        if (materias == null) {
            throw new IllegalArgumentException("La lista de materias no puede ser nula.");
        }
        this.materias = Collections.unmodifiableList(new ArrayList<>(materias));
    }

    /** Suma de los créditos de todas las materias del semestre. */
    public int getCreditosTotales() {
        int total = 0;
        for (MateriaPensum materia : materias) {
            total += materia.getCreditos();
        }
        return total;
    }
}
