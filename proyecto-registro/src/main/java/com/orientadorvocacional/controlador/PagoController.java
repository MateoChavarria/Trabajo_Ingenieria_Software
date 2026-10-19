package com.orientadorvocacional.controlador;

import com.orientadorvocacional.excepciones.AccesoYaOtorgadoException;
import com.orientadorvocacional.excepciones.OperacionPagoFallidaException;
import com.orientadorvocacional.excepciones.PagoNoEncontradoException;
import com.orientadorvocacional.excepciones.PagoYaProcesadoException;
import com.orientadorvocacional.modelo.DatosTarjeta;
import com.orientadorvocacional.modelo.OfertaAcceso;
import com.orientadorvocacional.modelo.Pago;
import com.orientadorvocacional.seguridad.InterceptorAutenticacion;
import com.orientadorvocacional.servicio.CasoUsoCancelarPago;
import com.orientadorvocacional.servicio.CasoUsoConsultarPago;
import com.orientadorvocacional.servicio.CasoUsoIniciarPago;
import com.orientadorvocacional.servicio.CasoUsoObtenerOfertaAcceso;
import com.orientadorvocacional.servicio.CasoUsoProcesarPago;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Expone por HTTP el flujo de pago (pasarela simulada):
 *   GET  /api/pagos/oferta                  precio, funciones y si ya tiene acceso
 *   POST /api/pagos/iniciar                 crea el pago pendiente
 *   POST /api/pagos/{referencia}/procesar   cobra con los datos de la tarjeta
 *   POST /api/pagos/{referencia}/cancelar   el usuario sale sin pagar
 *   GET  /api/pagos/{referencia}            resultado (pantalla de confirmacion)
 *
 * El id del usuario sale siempre del token (nunca del cliente), igual
 * que en los demas controladores.
 */
@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final CasoUsoObtenerOfertaAcceso casoUsoOferta;
    private final CasoUsoIniciarPago casoUsoIniciar;
    private final CasoUsoProcesarPago casoUsoProcesar;
    private final CasoUsoCancelarPago casoUsoCancelar;
    private final CasoUsoConsultarPago casoUsoConsultar;

    public PagoController(CasoUsoObtenerOfertaAcceso casoUsoOferta,
                          CasoUsoIniciarPago casoUsoIniciar,
                          CasoUsoProcesarPago casoUsoProcesar,
                          CasoUsoCancelarPago casoUsoCancelar,
                          CasoUsoConsultarPago casoUsoConsultar) {
        this.casoUsoOferta = casoUsoOferta;
        this.casoUsoIniciar = casoUsoIniciar;
        this.casoUsoProcesar = casoUsoProcesar;
        this.casoUsoCancelar = casoUsoCancelar;
        this.casoUsoConsultar = casoUsoConsultar;
    }

    /** Datos que el usuario escribe en la pasarela. toString() oculta todo para que nunca salgan en un log. */
    public record ProcesarPagoRequest(String nombreTitular, String numeroTarjeta, String vencimiento, String cvv) {
        @Override
        public String toString() {
            return "ProcesarPagoRequest[ocultos]";
        }
    }

    /** Lo que se devuelve de un pago: no incluye el id del usuario. */
    public record PagoDTO(String referencia, int monto, String moneda, String estado, String motivoFallo,
                          LocalDateTime fecha, LocalDateTime fechaResolucion) {
        static PagoDTO desde(Pago pago) {
            return new PagoDTO(
                    pago.getReferencia(),
                    pago.getMonto(),
                    OfertaAcceso.MONEDA,
                    pago.getEstado().aTextoBaseDeDatos(),
                    pago.getMotivoFallo(),
                    pago.getFechaCreacion(),
                    pago.getFechaResolucion());
        }
    }

    @GetMapping("/oferta")
    public ResponseEntity<?> obtenerOferta(
            @RequestAttribute(InterceptorAutenticacion.ATRIBUTO_USUARIO_ID) String usuarioId) {
        try {
            return ResponseEntity.ok(casoUsoOferta.ejecutar(usuarioId));
        } catch (OperacionPagoFallidaException excepcion) {
            return ResponseEntity.internalServerError().body(excepcion.getMessage());
        }
    }

    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarPago(
            @RequestAttribute(InterceptorAutenticacion.ATRIBUTO_USUARIO_ID) String usuarioId) {
        try {
            return ResponseEntity.ok(PagoDTO.desde(casoUsoIniciar.ejecutar(usuarioId)));
        } catch (AccesoYaOtorgadoException excepcion) {
            return ResponseEntity.status(409).body(excepcion.getMessage());
        } catch (OperacionPagoFallidaException excepcion) {
            return ResponseEntity.internalServerError().body(excepcion.getMessage());
        }
    }

    /**
     * Procesa el cobro. Un pago rechazado NO es un error HTTP: responde 200
     * con estado "fallido" y el motivo, para que la pantalla de confirmacion
     * lo muestre. Si los datos de la tarjeta tienen mal formato, responde 400
     * (el pago sigue pendiente y el usuario puede corregirlos).
     */
    @PostMapping("/{referencia}/procesar")
    public ResponseEntity<?> procesarPago(
            @RequestAttribute(InterceptorAutenticacion.ATRIBUTO_USUARIO_ID) String usuarioId,
            @PathVariable String referencia,
            @RequestBody ProcesarPagoRequest datos) {
        if (!Pago.esReferenciaValida(referencia)) {
            return ResponseEntity.badRequest().body("La referencia del pago no es válida.");
        }
        if (datos == null) {
            return ResponseEntity.badRequest().body("Debes escribir los datos de la tarjeta.");
        }
        try {
            // El constructor llama a los setters, que validan el formato (IllegalArgumentException -> 400).
            DatosTarjeta tarjeta = new DatosTarjeta(
                    datos.nombreTitular(), datos.numeroTarjeta(), datos.vencimiento(), datos.cvv());

            return ResponseEntity.ok(PagoDTO.desde(casoUsoProcesar.ejecutar(usuarioId, referencia, tarjeta)));

        } catch (PagoNoEncontradoException excepcion) {
            return ResponseEntity.status(404).body(excepcion.getMessage());
        } catch (PagoYaProcesadoException excepcion) {
            return ResponseEntity.status(409).body(excepcion.getMessage());
        } catch (OperacionPagoFallidaException excepcion) {
            return ResponseEntity.internalServerError().body(excepcion.getMessage());
        }
    }

    @PostMapping("/{referencia}/cancelar")
    public ResponseEntity<?> cancelarPago(
            @RequestAttribute(InterceptorAutenticacion.ATRIBUTO_USUARIO_ID) String usuarioId,
            @PathVariable String referencia) {
        if (!Pago.esReferenciaValida(referencia)) {
            return ResponseEntity.badRequest().body("La referencia del pago no es válida.");
        }
        try {
            return ResponseEntity.ok(PagoDTO.desde(casoUsoCancelar.ejecutar(usuarioId, referencia)));
        } catch (PagoNoEncontradoException excepcion) {
            return ResponseEntity.status(404).body(excepcion.getMessage());
        } catch (PagoYaProcesadoException excepcion) {
            return ResponseEntity.status(409).body(excepcion.getMessage());
        } catch (OperacionPagoFallidaException excepcion) {
            return ResponseEntity.internalServerError().body(excepcion.getMessage());
        }
    }

    @GetMapping("/{referencia}")
    public ResponseEntity<?> consultarPago(
            @RequestAttribute(InterceptorAutenticacion.ATRIBUTO_USUARIO_ID) String usuarioId,
            @PathVariable String referencia) {
        if (!Pago.esReferenciaValida(referencia)) {
            return ResponseEntity.badRequest().body("La referencia del pago no es válida.");
        }
        try {
            return ResponseEntity.ok(PagoDTO.desde(casoUsoConsultar.ejecutar(usuarioId, referencia)));
        } catch (PagoNoEncontradoException excepcion) {
            return ResponseEntity.status(404).body(excepcion.getMessage());
        } catch (OperacionPagoFallidaException excepcion) {
            return ResponseEntity.internalServerError().body(excepcion.getMessage());
        }
    }
}
