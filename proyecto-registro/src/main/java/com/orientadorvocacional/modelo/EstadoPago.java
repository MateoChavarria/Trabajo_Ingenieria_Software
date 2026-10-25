package com.orientadorvocacional.modelo;

/**
 * Estados posibles de un pago. Se guarda en minusculas en la columna
 * "estado" de la tabla "pagos".
 *
 *   PENDIENTE: se inicio el pago y todavia no hay resultado.
 *   EXITOSO:   la pasarela aprobo el cobro (el usuario recibe acceso completo).
 *   FALLIDO:   la pasarela rechazo el cobro.
 *   CANCELADO: el usuario salio de la pasarela sin pagar.
 */
public enum EstadoPago {
    PENDIENTE,
    EXITOSO,
    FALLIDO,
    CANCELADO;

    /** Formato (minusculas) que se guarda en la base de datos. */
    public String aTextoBaseDeDatos() {
        return this.name().toLowerCase();
    }

    /** Convierte el texto de la base de datos al estado correspondiente. */
    public static EstadoPago desdeTextoBaseDeDatos(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("El estado del pago no puede estar vacío.");
        }
        return EstadoPago.valueOf(texto.trim().toUpperCase());
    }
}
