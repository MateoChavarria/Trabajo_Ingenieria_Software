package com.orientadorvocacional.controlador;

import com.orientadorvocacional.excepciones.CuestionarioFallidoException;
import com.orientadorvocacional.excepciones.ResultadoTestFallidoException;
import com.orientadorvocacional.modelo.Pregunta;
import com.orientadorvocacional.servicio.CasoUsoCalcularYGuardarResultadoTest;
import com.orientadorvocacional.servicio.CasoUsoObtenerCuestionario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Expone por HTTP el cuestionario del test vocacional y el endpoint
 * que calcula y guarda el resultado una vez el usuario termina de
 * responder.
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    private final CasoUsoObtenerCuestionario casoUsoObtenerCuestionario;
    private final CasoUsoCalcularYGuardarResultadoTest casoUsoCalcularResultado;

    public TestController(CasoUsoObtenerCuestionario casoUsoObtenerCuestionario,
                           CasoUsoCalcularYGuardarResultadoTest casoUsoCalcularResultado) {
        this.casoUsoObtenerCuestionario = casoUsoObtenerCuestionario;
        this.casoUsoCalcularResultado = casoUsoCalcularResultado;
    }

    /** Una respuesta individual: a que pregunta pertenece y que opcion se eligio. */
    public record RespuestaDTO(int preguntaId, int opcionId) {
    }

    /** Cuerpo completo que manda el frontend al terminar el test. */
    public record EnviarRespuestasRequest(String usuarioId, List<RespuestaDTO> respuestas) {
    }

    @GetMapping("/preguntas")
    public ResponseEntity<?> obtenerCuestionario() {
        try {
            List<Pregunta> preguntas = casoUsoObtenerCuestionario.ejecutar();
            return ResponseEntity.ok(preguntas);

        } catch (CuestionarioFallidoException excepcion) {
            return ResponseEntity.internalServerError().body(excepcion.getMessage());
        }
    }

    @PostMapping("/resultado")
    public ResponseEntity<?> enviarRespuestas(@RequestBody EnviarRespuestasRequest datos) {
        try {
            // Convertimos la lista de RespuestaDTO que llega del frontend
            // a un mapa (preguntaId -> opcionId), que es el formato que
            // espera el caso de uso.
            Map<Integer, Integer> mapaRespuestas = new HashMap<>();
            for (RespuestaDTO respuesta : datos.respuestas()) {
                mapaRespuestas.put(respuesta.preguntaId(), respuesta.opcionId());
            }

            Map<String, Integer> resultado =
                    casoUsoCalcularResultado.ejecutar(datos.usuarioId(), mapaRespuestas);

            return ResponseEntity.ok(resultado);

        } catch (CuestionarioFallidoException | ResultadoTestFallidoException excepcion) {
            return ResponseEntity.internalServerError().body(excepcion.getMessage());
        }
    }
}
