package com.orientadorvocacional.servicio;

import com.orientadorvocacional.modelo.DatosTarjeta;
import com.orientadorvocacional.modelo.ResultadoCobro;

/**
 * Contrato con la pasarela de pagos. Los casos de uso dependen de esta
 * interfaz, no de una pasarela concreta: hoy se usa PasarelaSimulada y,
 * si algun dia se conecta una pasarela real (Wompi, PayU...), solo hay
 * que escribir otra implementacion sin tocar el resto del sistema.
 */
public interface IPasarelaPagos {

    /**
     * Intenta cobrar el monto a la tarjeta.
     *
     * @param referencia codigo de la transaccion, para que la pasarela la identifique
     */
    ResultadoCobro cobrar(DatosTarjeta tarjeta, int monto, String referencia);
}
