package com.orientadorvocacional.modelo;

import java.util.List;

/**
 * Envoltorio de la respuesta completa del listado de recomendaciones:
 * las carreras ordenadas, mas la bandera "afinidadBaja" que resuelve
 * la tarea tecnica "definir que pasa si ninguna carrera tiene un
 * puntaje suficientemente alto".
 */
public class ResultadoRecomendacion {

    private final List<CarreraRecomendada> carreras;
    private final boolean afinidadBaja;

    public ResultadoRecomendacion(List<CarreraRecomendada> carreras, boolean afinidadBaja) {
        this.carreras = carreras;
        this.afinidadBaja = afinidadBaja;
    }

    public List<CarreraRecomendada> getCarreras() {
        return carreras;
    }

    public boolean isAfinidadBaja() {
        return afinidadBaja;
    }
}