package com.orientadorvocacional.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "resultados_test")
public class ResultadoTestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * UUID (no String): la columna "usuario_id" en PostgreSQL es de
     * tipo uuid nativo (por la referencia a auth.users). Usar
     * java.util.UUID aca hace que Hibernate mapee correctamente ese
     * tipo, en vez de intentar tratarlo como texto (varchar).
     */
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "detalle", nullable = false, columnDefinition = "text")
    private String detalle;

    protected ResultadoTestEntity() {
    }

    public ResultadoTestEntity(UUID usuarioId, LocalDateTime fecha, String detalle) {
        this.usuarioId = usuarioId;
        this.fecha = fecha;
        this.detalle = detalle;
    }

    public Long getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getDetalle() {
        return detalle;
    }
}