package com.orientadorvocacional.repositorio;

import com.orientadorvocacional.excepciones.ConsultaTemasDificilesFallidaException;

import java.util.List;
import java.util.Map;

public interface ITemasDificilesRepositorio {

    /**
     * Los temas más difíciles de todas las materias de una carrera,
     * organizados así: número de semestre, luego nombre de la materia, y
     * al final la lista de temas en su orden. Una materia sin temas
     * simplemente no aparece.
     */
    Map<Integer, Map<String, List<String>>> obtenerPorCarrera(int carreraId)
            throws ConsultaTemasDificilesFallidaException;
}
