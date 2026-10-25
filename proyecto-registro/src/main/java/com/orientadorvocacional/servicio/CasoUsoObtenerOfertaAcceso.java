package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.OperacionPagoFallidaException;
import com.orientadorvocacional.modelo.OfertaAcceso;
import com.orientadorvocacional.repositorio.IRepositorioAcceso;
import org.springframework.stereotype.Service;

/**
 * Devuelve lo que se vende (precio y funciones que se desbloquean) y si
 * el usuario que consulta ya tiene acceso completo. La pantalla de pago
 * y los candados de la interfaz se apoyan en esta respuesta.
 */
@Service
public class CasoUsoObtenerOfertaAcceso {

    private final IRepositorioAcceso repositorioAcceso;

    public CasoUsoObtenerOfertaAcceso(IRepositorioAcceso repositorioAcceso) {
        this.repositorioAcceso = repositorioAcceso;
    }

    public OfertaAcceso ejecutar(String usuarioId) throws OperacionPagoFallidaException {
        return new OfertaAcceso(repositorioAcceso.tieneAccesoCompleto(usuarioId));
    }
}
