package com.orientadorvocacional.modelo;

/**
 * Une una Carrera con el nivel de afinidad calculado para un usuario
 * y el texto que explica por que se le recomienda (Historia 5:
 * "pantalla de detalle... por que se la recomendo").
 */
public class CarreraRecomendada {

    private final Carrera carrera;
    private final int afinidad;
    private final String motivo;

    public CarreraRecomendada(Carrera carrera, int afinidad, String motivo) {
        this.carrera = carrera;
        this.afinidad = afinidad;
        this.motivo = motivo;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public int getAfinidad() {
        return afinidad;
    }

    public String getMotivo() {
        return motivo;
    }
}