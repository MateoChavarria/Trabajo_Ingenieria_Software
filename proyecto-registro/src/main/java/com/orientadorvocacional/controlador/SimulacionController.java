package com.orientadorvocacional.controlador;

import com.orientadorvocacional.excepciones.CarreraNoEncontradaException;
import com.orientadorvocacional.excepciones.ConsultaCarrerasFallidaException;
import com.orientadorvocacional.excepciones.ConsultaPensumFallidaException;
import com.orientadorvocacional.excepciones.ConsultaTemasDificilesFallidaException;
import com.orientadorvocacional.excepciones.ConsultaUniversidadesFallidaException;
import com.orientadorvocacional.excepciones.PensumNoDisponibleException;
import com.orientadorvocacional.servicio.CasoUsoSimularCarrera;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simulación de una carrera semestre por semestre. Comparte la ruta
 * base /api/carreras, pero va en su propia clase para que cada
 * controlador tenga una sola responsabilidad.
 */
@RestController
@RequestMapping("/api/carreras")
public class SimulacionController {

    private final CasoUsoSimularCarrera casoUsoSimular;

    public SimulacionController(CasoUsoSimularCarrera casoUsoSimular) {
        this.casoUsoSimular = casoUsoSimular;
    }

    /** Público: no requiere sesión (el pensum tampoco la requiere). Usado por simulador.html. */
    @GetMapping("/{id}/simulacion")
    public ResponseEntity<?> obtenerSimulacion(@PathVariable int id) {
        if (id <= 0) {
            return ResponseEntity.badRequest().body("El id de la carrera no es válido.");
        }
        try {
            return ResponseEntity.ok(casoUsoSimular.ejecutar(id));
        } catch (CarreraNoEncontradaException | PensumNoDisponibleException excepcion) {
            return ResponseEntity.status(404).body(excepcion.getMessage());
        } catch (ConsultaCarrerasFallidaException | ConsultaUniversidadesFallidaException
                 | ConsultaPensumFallidaException | ConsultaTemasDificilesFallidaException excepcion) {
            return ResponseEntity.internalServerError().body(excepcion.getMessage());
        }
    }
}
