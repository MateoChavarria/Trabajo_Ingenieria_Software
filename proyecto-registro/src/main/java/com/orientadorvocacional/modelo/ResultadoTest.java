package com.orientadorvocacional.modelo;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Representa el resultado de un test vocacional ya calculado: cuanto
 * puntaje sumo el usuario en cada area de interes (por ejemplo,
 * {"ingenieria": 8, "salud": 3}).
 *
 * Mismo patron de encapsulamiento que las demas clases del proyecto:
 * el constructor delega la validacion a los setters, no la hace el
 * mismo directamente.
 */
public class ResultadoTest {

    private String usuarioId;
    private LocalDateTime fecha;
    private Map<String, Integer> detallePorArea;

    public ResultadoTest(String usuarioId, Map<String, Integer> detallePorArea) {
        setUsuarioId(usuarioId);
        setDetallePorArea(detallePorArea);
        this.fecha = LocalDateTime.now();
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        if (usuarioId == null || usuarioId.isBlank()) {
            throw new IllegalArgumentException("El id del usuario no puede estar vacío.");
        }
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Map<String, Integer> getDetallePorArea() {
        return detallePorArea;
    }

    public void setDetallePorArea(Map<String, Integer> detallePorArea) {
        if (detallePorArea == null || detallePorArea.isEmpty()) {
            throw new IllegalArgumentException("El detalle del resultado no puede estar vacío.");
        }
        this.detallePorArea = detallePorArea;
    }

        public void setFecha(LocalDateTime fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula.");
        }
        this.fecha = fecha;
    }
}
