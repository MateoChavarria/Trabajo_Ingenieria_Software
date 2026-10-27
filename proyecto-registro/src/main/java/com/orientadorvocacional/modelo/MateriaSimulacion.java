package com.orientadorvocacional.modelo;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Una materia tal como se muestra en la simulación: además de su nombre,
 * créditos y enfoque, trae los temas que más suelen costar (hasta 3).
 *
 * Mismo patrón del proyecto: el constructor NO valida, delega en los
 * setters, y los setters son los que validan.
 */
public class MateriaSimulacion {

    public static final int MAXIMO_TEMAS_DIFICILES = 3;

    /**
     * Las materias electivas u optativas son de libre elección: sus temas
     * difíciles dependen de la materia que el estudiante escoja, así que
     * no pueden tener temas propios. Se reconocen por el nombre
     * (sin importar tildes ni mayúsculas): "Electiva I", "Optativa CBD"...
     */
    public static boolean esNombreDeMateriaElectiva(String nombre) {
        if (nombre == null) {
            return false;
        }
        String normalizado = Normalizer.normalize(nombre, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
        return normalizado.contains("electiv") || normalizado.contains("optativ");
    }

    private String nombre;
    private int creditos;
    private String enfoqueTematico;
    private List<String> temasDificiles;

    public MateriaSimulacion(String nombre, int creditos, String enfoqueTematico,
                             List<String> temasDificiles) {
        setNombre(nombre);
        setCreditos(creditos);
        setEnfoqueTematico(enfoqueTematico);
        setTemasDificiles(temasDificiles);
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

    /** True si la materia es de libre elección (electiva u optativa). */
    public boolean isElectiva() {
        return esNombreDeMateriaElectiva(nombre);
    }

    public List<String> getTemasDificiles() {
        return temasDificiles;
    }

    /**
     * La lista puede estar vacía (materia sin temas registrados), pero no tener más de 3
     * ni textos vacíos. Una materia electiva u optativa no puede tener temas.
     */
    public void setTemasDificiles(List<String> temasDificiles) {
        if (temasDificiles == null) {
            throw new IllegalArgumentException("La lista de temas no puede ser nula.");
        }
        if (temasDificiles.size() > MAXIMO_TEMAS_DIFICILES) {
            throw new IllegalArgumentException(
                    "Una materia no puede tener más de " + MAXIMO_TEMAS_DIFICILES + " temas difíciles.");
        }
        if (isElectiva() && !temasDificiles.isEmpty()) {
            throw new IllegalArgumentException(
                    "Una materia electiva u optativa no tiene temas difíciles propios: dependen de la materia que se escoja.");
        }
        for (String tema : temasDificiles) {
            if (tema == null || tema.isBlank()) {
                throw new IllegalArgumentException("Un tema difícil no puede estar vacío.");
            }
        }
        this.temasDificiles = Collections.unmodifiableList(new ArrayList<>(temasDificiles));
    }
}
