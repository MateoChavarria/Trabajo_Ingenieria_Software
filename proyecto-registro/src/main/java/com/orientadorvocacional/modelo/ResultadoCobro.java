package com.orientadorvocacional.modelo;

/**
 * Respuesta de la pasarela de pagos a un intento de cobro: aprobado,
 * o rechazado con el motivo. No tiene setters: una vez que la pasarela
 * responde, el resultado no cambia.
 */
public final class ResultadoCobro {

    private final boolean aprobado;
    private final String motivo;

    private ResultadoCobro(boolean aprobado, String motivo) {
        this.aprobado = aprobado;
        this.motivo = motivo;
    }

    public static ResultadoCobro aprobado() {
        return new ResultadoCobro(true, null);
    }

    public static ResultadoCobro rechazado(String motivo) {
        return new ResultadoCobro(false, motivo);
    }

    public boolean isAprobado() {
        return aprobado;
    }

    /** Solo tiene valor si el cobro fue rechazado. */
    public String getMotivo() {
        return motivo;
    }
}
