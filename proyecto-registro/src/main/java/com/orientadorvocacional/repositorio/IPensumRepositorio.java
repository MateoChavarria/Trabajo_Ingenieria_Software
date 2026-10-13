package com.orientadorvocacional.repositorio;

import com.orientadorvocacional.excepciones.ConsultaPensumFallidaException;
import com.orientadorvocacional.modelo.Carrera;
import com.orientadorvocacional.modelo.PensumCarrera;

public interface IPensumRepositorio {

    /** Arma el plan de estudios de la carrera: semestres en orden, cada uno con sus materias. */
    PensumCarrera obtenerPorCarrera(Carrera carrera) throws ConsultaPensumFallidaException;
}
