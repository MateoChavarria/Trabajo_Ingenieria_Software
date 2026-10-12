package com.orientadorvocacional.repositorio;

import com.orientadorvocacional.excepciones.ConsultaUniversidadesFallidaException;
import com.orientadorvocacional.modelo.Universidad;

import java.util.List;

public interface IUniversidadRepositorio {

    /** Todas las universidades, en orden alfabético. */
    List<Universidad> obtenerTodas() throws ConsultaUniversidadesFallidaException;

    /**
     * Indica si la carrera pertenece a una universidad con información
     * completa (hoy, la UPB Medellín). Si la carrera no existe o no
     * tiene universidad asignada, devuelve false.
     */
    boolean carreraPerteneceAUniversidadConInformacionCompleta(int carreraId)
            throws ConsultaUniversidadesFallidaException;
}
