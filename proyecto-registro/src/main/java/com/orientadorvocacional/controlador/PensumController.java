package com.orientadorvocacional.controlador;

import com.orientadorvocacional.excepciones.CarreraNoEncontradaException;
import com.orientadorvocacional.excepciones.ConsultaCarrerasFallidaException;
import com.orientadorvocacional.excepciones.ConsultaPensumFallidaException;
import com.orientadorvocacional.excepciones.ConsultaUniversidadesFallidaException;
import com.orientadorvocacional.excepciones.PensumNoDisponibleException;
import com.orientadorvocacional.servicio.CasoUsoObtenerPensumCarrera;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Plan de estudios de una carrera. Comparte la ruta base /api/carreras
 * con CarreraController, pero va en su propia clase para que cada
 * controlador tenga una sola responsabilidad.
 */
@RestController
@RequestMapping("/api/carreras")
public class PensumController {

    private final CasoUsoObtenerPensumCarrera casoUsoPensum;

    public PensumController(CasoUsoObtenerPensumCarrera casoUsoPensum) {
        this.casoUsoPensum = casoUsoPensum;
    }

    /** Público: no requiere sesión. Usado por carrera-detalle.html. */
    @GetMapping("/{id}/pensum")
    public ResponseEntity<?> obtenerPensum(@PathVariable int id) {
        if (id <= 0) {
            return ResponseEntity.badRequest().body("El id de la carrera no es válido.");
        }
        try {
            return ResponseEntity.ok(casoUsoPensum.ejecutar(id));
        } catch (CarreraNoEncontradaException | PensumNoDisponibleException excepcion) {
            return ResponseEntity.status(404).body(excepcion.getMessage());
        } catch (ConsultaCarrerasFallidaException | ConsultaUniversidadesFallidaException
                 | ConsultaPensumFallidaException excepcion) {
            return ResponseEntity.internalServerError().body(excepcion.getMessage());
        }
    }
}
