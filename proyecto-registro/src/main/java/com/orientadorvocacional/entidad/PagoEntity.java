package com.orientadorvocacional.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/** Fila de la tabla "pagos" de Supabase (un intento de pago). */
@Entity
@Table(name = "pagos")
public class PagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** UUID nativo, igual que en resultados_test. */
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "monto", nullable = false)
    private Integer monto;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "referencia", nullable = false, unique = true)
    private String referencia;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @Column(name = "motivo_fallo")
    private String motivoFallo;

    protected PagoEntity() {
    }

    public PagoEntity(UUID usuarioId, Integer monto, String estado, String referencia,
                      LocalDateTime fecha, LocalDateTime fechaResolucion, String motivoFallo) {
        this.usuarioId = usuarioId;
        this.monto = monto;
        this.estado = estado;
        this.referencia = referencia;
        this.fecha = fecha;
        this.fechaResolucion = fechaResolucion;
        this.motivoFallo = motivoFallo;
    }

    public Long getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public Integer getMonto() {
        return monto;
    }

    public String getEstado() {
        return estado;
    }

    public String getReferencia() {
        return referencia;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public LocalDateTime getFechaResolucion() {
        return fechaResolucion;
    }

    public String getMotivoFallo() {
        return motivoFallo;
    }
}
