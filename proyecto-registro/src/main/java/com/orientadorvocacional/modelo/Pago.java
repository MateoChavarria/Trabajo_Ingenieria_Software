package com.orientadorvocacional.modelo;

import com.orientadorvocacional.excepciones.PagoYaProcesadoException;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Un intento de pago de un usuario. Cada vez que el usuario pulsa
 * "Pagar" se crea un Pago nuevo en estado PENDIENTE, y termina en
 * EXITOSO, FALLIDO o CANCELADO. Una vez resuelto, ya no cambia.
 *
 * Encapsulamiento: todos los campos son privados. Los setters validan;
 * los constructores solo los llaman (no validan por si mismos). Los
 * cambios de estado se hacen unicamente con marcarExitoso(),
 * marcarFallido() y marcarCancelado(), que exigen que el pago siga
 * pendiente.
 */
public class Pago {

    /** Formato de la referencia: "PAY-" y 12 caracteres hexadecimales en mayusculas. */
    private static final Pattern PATRON_REFERENCIA = Pattern.compile("^PAY-[0-9A-F]{12}$");

    public static final int LONGITUD_MAXIMA_MOTIVO = 200;

    private String usuarioId;
    private int monto;
    private String referencia;
    private EstadoPago estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaResolucion;
    private String motivoFallo;

    /** Pago nuevo: queda PENDIENTE, con referencia unica y fecha de ahora. */
    public Pago(String usuarioId, int monto) {
        setUsuarioId(usuarioId);
        setMonto(monto);
        setReferencia(generarReferencia());
        setEstado(EstadoPago.PENDIENTE);
        setFechaCreacion(LocalDateTime.now());
    }

    /** Reconstruye un pago ya guardado en la base de datos. */
    public Pago(String usuarioId, int monto, String referencia, EstadoPago estado,
                LocalDateTime fechaCreacion, LocalDateTime fechaResolucion, String motivoFallo) {
        setUsuarioId(usuarioId);
        setMonto(monto);
        setReferencia(referencia);
        setEstado(estado);
        setFechaCreacion(fechaCreacion);
        setFechaResolucion(fechaResolucion);
        setMotivoFallo(motivoFallo);
    }

    /** Genera una referencia unica e imposible de adivinar para la transaccion. */
    public static String generarReferencia() {
        String aleatorio = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        return "PAY-" + aleatorio.toUpperCase();
    }

    /** Indica si un texto tiene el formato de una referencia de pago. */
    public static boolean esReferenciaValida(String texto) {
        return texto != null && PATRON_REFERENCIA.matcher(texto).matches();
    }

    // ---------- Cambios de estado ----------

    public void marcarExitoso() throws PagoYaProcesadoException {
        exigirPendiente();
        this.estado = EstadoPago.EXITOSO;
        this.fechaResolucion = LocalDateTime.now();
    }

    public void marcarFallido(String motivo) throws PagoYaProcesadoException {
        exigirPendiente();
        this.estado = EstadoPago.FALLIDO;
        this.fechaResolucion = LocalDateTime.now();
        setMotivoFallo(motivo);
    }

    public void marcarCancelado() throws PagoYaProcesadoException {
        exigirPendiente();
        this.estado = EstadoPago.CANCELADO;
        this.fechaResolucion = LocalDateTime.now();
    }

    private void exigirPendiente() throws PagoYaProcesadoException {
        if (estado != EstadoPago.PENDIENTE) {
            throw new PagoYaProcesadoException("Este pago ya fue procesado y no se puede modificar.");
        }
    }

    // ---------- Getters y setters (los setters validan) ----------

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        if (usuarioId == null || usuarioId.isBlank()) {
            throw new IllegalArgumentException("El id del usuario no puede estar vacío.");
        }
        this.usuarioId = usuarioId;
    }

    public int getMonto() {
        return monto;
    }

    public void setMonto(int monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor que cero.");
        }
        this.monto = monto;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        if (!esReferenciaValida(referencia)) {
            throw new IllegalArgumentException("La referencia del pago no tiene un formato válido.");
        }
        this.referencia = referencia;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado del pago no puede ser nulo.");
        }
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        if (fechaCreacion == null) {
            throw new IllegalArgumentException("La fecha del pago no puede ser nula.");
        }
        this.fechaCreacion = fechaCreacion;
    }

    /** Es nula mientras el pago esta pendiente. */
    public LocalDateTime getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(LocalDateTime fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    /** Solo tiene valor cuando el pago fallo. */
    public String getMotivoFallo() {
        return motivoFallo;
    }

    public void setMotivoFallo(String motivoFallo) {
        if (motivoFallo == null || motivoFallo.isBlank()) {
            this.motivoFallo = null;
            return;
        }
        String limpio = motivoFallo.trim();
        this.motivoFallo = limpio.length() > LONGITUD_MAXIMA_MOTIVO
                ? limpio.substring(0, LONGITUD_MAXIMA_MOTIVO)
                : limpio;
    }
}
