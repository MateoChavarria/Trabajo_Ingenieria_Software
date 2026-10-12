package com.orientadorvocacional.controlador;

import com.orientadorvocacional.excepciones.ConsultaUniversidadesFallidaException;
import com.orientadorvocacional.servicio.CasoUsoListarUniversidades;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/universidades")
public class UniversidadController {

    private final CasoUsoListarUniversidades casoUsoListar;

    public UniversidadController(CasoUsoListarUniversidades casoUsoListar) {
        this.casoUsoListar = casoUsoListar;
    }

    /** Público: no requiere sesión. Usado por universidades.html. */
    @GetMapping
    public ResponseEntity<?> listarTodas() {
        try {
            return ResponseEntity.ok(casoUsoListar.ejecutar());
        } catch (ConsultaUniversidadesFallidaException excepcion) {
            return ResponseEntity.internalServerError().body(excepcion.getMessage());
        }
    }
}
