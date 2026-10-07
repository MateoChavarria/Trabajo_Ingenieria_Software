package com.orientadorvocacional.controlador;

import com.orientadorvocacional.modelo.PerfilVocacional;
import com.orientadorvocacional.servicio.CasoUsoObtenerPerfilVocacional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/perfil")
public class PerfilController {

    private final CasoUsoObtenerPerfilVocacional casoUso;

    public PerfilController(CasoUsoObtenerPerfilVocacional casoUso) {
        this.casoUso = casoUso;
    }

    public record AfinidadRequest(Map<String, Integer> afinidadPorArea) {
    }

    @PostMapping("/resumen")
    public ResponseEntity<PerfilVocacional> obtenerResumen(@RequestBody AfinidadRequest datos) {
        return ResponseEntity.ok(casoUso.ejecutar(datos.afinidadPorArea()));
    }
}