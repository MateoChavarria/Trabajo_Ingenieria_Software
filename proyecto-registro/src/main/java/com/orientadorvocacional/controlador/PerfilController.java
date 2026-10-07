package com.orientadorvocacional.controlador;

import com.orientadorvocacional.excepciones.ResultadoTestFallidoException;
import com.orientadorvocacional.modelo.HistorialTestItem;
import com.orientadorvocacional.modelo.PerfilVocacional;
import com.orientadorvocacional.servicio.CasoUsoObtenerHistorialTest;
import com.orientadorvocacional.servicio.CasoUsoObtenerPerfilVocacional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/perfil")
public class PerfilController {

    private final CasoUsoObtenerPerfilVocacional casoUsoPerfil;
    private final CasoUsoObtenerHistorialTest casoUsoHistorial;

    public PerfilController(CasoUsoObtenerPerfilVocacional casoUsoPerfil,
                             CasoUsoObtenerHistorialTest casoUsoHistorial) {
        this.casoUsoPerfil = casoUsoPerfil;
        this.casoUsoHistorial = casoUsoHistorial;
    }

    public record AfinidadRequest(Map<String, Integer> afinidadPorArea) {
    }

    @PostMapping("/resumen")
    public ResponseEntity<PerfilVocacional> obtenerResumen(@RequestBody AfinidadRequest datos) {
        return ResponseEntity.ok(casoUsoPerfil.ejecutar(datos.afinidadPorArea()));
    }

    @GetMapping("/historial")
    public ResponseEntity<?> obtenerHistorial(@RequestParam String usuarioId) {
        try {
            List<HistorialTestItem> historial = casoUsoHistorial.ejecutar(usuarioId);
            return ResponseEntity.ok(historial);
        } catch (ResultadoTestFallidoException excepcion) {
            return ResponseEntity.internalServerError().body(excepcion.getMessage());
        }
    }
}