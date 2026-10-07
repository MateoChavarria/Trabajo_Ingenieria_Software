package com.orientadorvocacional.modelo;

/**
 * Representa una de las areas "dominantes" del perfil del usuario:
 * su codigo, nombre legible, afinidad calculada, y el texto que
 * interpreta que significa destacarse en esa area.
 */
public class AreaDominante {

    private final String codigo;
    private final String nombreAmigable;
    private final int afinidad;
    private final String interpretacion;

    public AreaDominante(String codigo, String nombreAmigable, int afinidad, String interpretacion) {
        this.codigo = codigo;
        this.nombreAmigable = nombreAmigable;
        this.afinidad = afinidad;
        this.interpretacion = interpretacion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombreAmigable() {
        return nombreAmigable;
    }

    public int getAfinidad() {
        return afinidad;
    }

    public String getInterpretacion() {
        return interpretacion;
    }
}