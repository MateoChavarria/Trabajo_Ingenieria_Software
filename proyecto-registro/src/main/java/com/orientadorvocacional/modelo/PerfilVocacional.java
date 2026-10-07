package com.orientadorvocacional.modelo;

import java.util.List;
import java.util.Map;

/**
 * Resumen completo del perfil vocacional de un usuario: sus areas
 * dominantes (con interpretacion) y el detalle de afinidad de TODAS
 * las areas, para poder mostrar tanto el resumen destacado como la
 * vista completa (tarjetas por area).
 */
public class PerfilVocacional {

    private final List<AreaDominante> areasDominantes;
    private final Map<String, Integer> afinidadPorArea;

    public PerfilVocacional(List<AreaDominante> areasDominantes, Map<String, Integer> afinidadPorArea) {
        this.areasDominantes = areasDominantes;
        this.afinidadPorArea = afinidadPorArea;
    }

    public List<AreaDominante> getAreasDominantes() {
        return areasDominantes;
    }

    public Map<String, Integer> getAfinidadPorArea() {
        return afinidadPorArea;
    }
}