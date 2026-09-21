package com.orientadorvocacional.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "correo", nullable = false, unique = true)
    private String correo;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "estado_cuenta", nullable = false)
    private String estadoCuenta;

    protected UsuarioEntity() {
    }

    public UsuarioEntity(UUID id, String correo, LocalDateTime fechaRegistro, String estadoCuenta) {
        this.id = id;
        this.correo = correo;
        this.fechaRegistro = fechaRegistro;
        this.estadoCuenta = estadoCuenta;
    }

    public UUID getId() {
        return id;
    }

    public String getCorreo() {
        return correo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public String getEstadoCuenta() {
        return estadoCuenta;
    }
}