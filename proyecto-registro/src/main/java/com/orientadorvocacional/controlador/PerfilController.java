package com.orientadorvocacional.controlador;

import com.orientadorvocacional.excepciones.ResultadoTestFallidoException;
import com.orientadorvocacional.modelo.HistorialTestItem;
import com.orientadorvocacional.modelo.PerfilVocacional;
import com.orientadorvocacional.seguridad.InterceptorAutenticacion;
import com.orientadorvocacional.servicio.CasoUsoObtenerHistorialTest;
import com.orientadorvocacional.servicio.CasoUsoObtenerPerfilVocacional;
import com.orientadorvocacional.servicio.ValidadorAfinidad;
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
        ValidadorAfinidad.validar(datos == null ? null : datos.afinidadPorArea());
        return ResponseEntity.ok(casoUsoPerfil.ejecutar(datos.afinidadPorArea()));
    }

    /**
     * Devuelve SOLO el historial del usuario dueño del token. Antes se
     * recibia ?usuarioId=... y cualquiera podia consultar el historial
     * de otra persona (IDOR).
     */
    @GetMapping("/historial")
    public ResponseEntity<?> obtenerHistorial(
            @RequestAttribute(InterceptorAutenticacion.ATRIBUTO_USUARIO_ID) String usuarioId) {
        try {
            List<HistorialTestItem> historial = casoUsoHistorial.ejecutar(usuarioId);
            return ResponseEntity.ok(historial);
        } catch (ResultadoTestFallidoException excepcion) {
            return ResponseEntity.internalServerError().body(excepcion.getMessage());
        }
    }
}