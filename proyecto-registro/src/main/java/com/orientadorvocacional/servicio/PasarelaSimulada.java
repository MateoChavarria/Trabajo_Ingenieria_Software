package com.orientadorvocacional.servicio;

import com.orientadorvocacional.modelo.DatosTarjeta;
import com.orientadorvocacional.modelo.ResultadoCobro;
import org.springframework.stereotype.Component;

/**
 * Pasarela de pagos SIMULADA (modo de pruebas): no se conecta a ningun
 * banco ni cobra dinero real. El resultado depende solo del numero de
 * tarjeta, para que la demostracion sea siempre repetible:
 *
 *   4242 4242 4242 4242  -> aprobada
 *   4000 0000 0000 0002  -> rechazada por fondos insuficientes
 *   4000 0000 0000 0069  -> rechazada por el banco emisor
 *   cualquier otra       -> rechazada (no es una tarjeta de prueba)
 *
 * El numero de la tarjeta solo se compara en memoria: no se guarda ni
 * se escribe en ningun log.
 */
@Component
public class PasarelaSimulada implements IPasarelaPagos {

    public static final String TARJETA_APROBADA = "4242424242424242";
    public static final String TARJETA_FONDOS_INSUFICIENTES = "4000000000000002";
    public static final String TARJETA_RECHAZADA_BANCO = "4000000000000069";

    @Override
    public ResultadoCobro cobrar(DatosTarjeta tarjeta, int monto, String referencia) {
        String numero = tarjeta.getNumeroTarjeta();

        if (TARJETA_APROBADA.equals(numero)) {
            return ResultadoCobro.aprobado();
        }
        if (TARJETA_FONDOS_INSUFICIENTES.equals(numero)) {
            return ResultadoCobro.rechazado("Fondos insuficientes.");
        }
        if (TARJETA_RECHAZADA_BANCO.equals(numero)) {
            return ResultadoCobro.rechazado("El banco emisor rechazó la tarjeta.");
        }
        return ResultadoCobro.rechazado(
                "Esta tarjeta no es una tarjeta de prueba. Usa una de las tarjetas indicadas en la pantalla.");
    }
}
