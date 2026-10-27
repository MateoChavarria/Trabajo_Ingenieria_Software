package com.orientadorvocacional.modelo;

import java.util.List;

/**
 * Lo que la aplicacion vende: el "acceso completo". Concentra en un solo
 * lugar el precio y la lista de funciones que se desbloquean, para que la
 * pantalla de pago y el backend siempre digan lo mismo.
 */
public class OfertaAcceso {

    /** Precio unico (pesos colombianos). El proyecto es academico: no se cobra dinero real. */
    public static final int PRECIO_COP = 20000;
    public static final String MONEDA = "COP";

    private static final List<String> FUNCIONES_DESBLOQUEADAS = List.of(
            "Test vocacional completo",
            "Resumen de tu perfil vocacional",
            "Carreras recomendadas según tus resultados",
            "Simulador de carrera semestre por semestre");

    private static final List<String> FUNCIONES_GRATUITAS = List.of(
            "Explorar las carreras y sus planes de estudio",
            "Ver las universidades");

    private boolean accesoCompleto;

    public OfertaAcceso(boolean accesoCompleto) {
        setAccesoCompleto(accesoCompleto);
    }

    public int getMonto() {
        return PRECIO_COP;
    }

    public String getMoneda() {
        return MONEDA;
    }

    public List<String> getFuncionesDesbloqueadas() {
        return FUNCIONES_DESBLOQUEADAS;
    }

    public List<String> getFuncionesGratuitas() {
        return FUNCIONES_GRATUITAS;
    }

    /** Si el usuario que consulta ya pago. */
    public boolean isAccesoCompleto() {
        return accesoCompleto;
    }

    public void setAccesoCompleto(boolean accesoCompleto) {
        this.accesoCompleto = accesoCompleto;
    }
}
