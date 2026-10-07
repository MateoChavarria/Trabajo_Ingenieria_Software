package com.orientadorvocacional.controlador;

import com.orientadorvocacional.excepciones.CarreraNoEncontradaException;
import com.orientadorvocacional.excepciones.ConsultaCarrerasFallidaException;
import com.orientadorvocacional.modelo.Carrera;
import com.orientadorvocacional.modelo.ResultadoRecomendacion;
import com.orientadorvocacional.servicio.CasoUsoListarTodasLasCarreras;
import com.orientadorvocacional.servicio.CasoUsoObtenerCarrerasRecomendadas;
import com.orientadorvocacional.servicio.CasoUsoObtenerDetalleCarrera;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/carreras")
public class CarreraController {

    private final CasoUsoObtenerCarrerasRecomendadas casoUsoRecomendadas;
    private final CasoUsoObtenerDetalleCarrera casoUsoDetalle;
    private final CasoUsoListarTodasLasCarreras casoUsoListar;

    public CarreraController(CasoUsoObtenerCarrerasRecomendadas casoUsoRecomendadas,
                              CasoUsoObtenerDetalleCarrera casoUsoDetalle,
                              CasoUsoListarTodasLasCarreras casoUsoListar) {
        this.casoUsoRecomendadas = casoUsoRecomendadas;
        this.casoUsoDetalle = casoUsoDetalle;
        this.casoUsoListar = casoUsoListar;
    }

    public record AfinidadRequest(Map<String, Integer> afinidadPorArea) {
    }

    /** Publico: no requiere sesion. Usado por explorar.html. */
    @GetMapping
    public ResponseEntity<?> listarTodas() {
        try {
            return ResponseEntity.ok(casoUsoListar.ejecutar());
        } catch (ConsultaCarrerasFallidaException excepcion) {
            return ResponseEntity.internalServerError().body(excepcion.getMessage());
        }
    }

    @PostMapping("/recomendadas")
    public ResponseEntity<?> obtenerRecomendadas(@RequestBody AfinidadRequest datos) {
        try {
            ResultadoRecomendacion resultado = casoUsoRecomendadas.ejecutar(datos.afinidadPorArea());
            return ResponseEntity.ok(resultado);
        } catch (ConsultaCarrerasFallidaException excepcion) {
            return ResponseEntity.internalServerError().body(excepcion.getMessage());
        }
    }

    /** Publico: no requiere sesion. Usado por carrera-detalle.html. */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDetalle(@PathVariable int id) {
        try {
            Carrera carrera = casoUsoDetalle.ejecutar(id);
            return ResponseEntity.ok(carrera);
        } catch (CarreraNoEncontradaException excepcion) {
            return ResponseEntity.status(404).body(excepcion.getMessage());
        } catch (ConsultaCarrerasFallidaException excepcion) {
            return ResponseEntity.internalServerError().body(excepcion.getMessage());
        }
    }
}