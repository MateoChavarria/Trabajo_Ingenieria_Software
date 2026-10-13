package com.orientadorvocacional.controlador;

import com.orientadorvocacional.excepciones.CuestionarioFallidoException;
import com.orientadorvocacional.excepciones.ResultadoTestFallidoException;
import com.orientadorvocacional.modelo.Pregunta;
import com.orientadorvocacional.seguridad.InterceptorAutenticacion;
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

    /** Limite defensivo: el cuestionario real tiene ~53 preguntas. */
    static final int MAXIMO_RESPUESTAS = 500;

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

    /**
     * Cuerpo completo que manda el frontend al terminar el test. El id
     * del usuario NO viaja aqui: se toma del token de sesion validado.
     */
    public record EnviarRespuestasRequest(List<RespuestaDTO> respuestas) {
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
    public ResponseEntity<?> enviarRespuestas(
            @RequestAttribute(InterceptorAutenticacion.ATRIBUTO_USUARIO_ID) String usuarioId,
            @RequestBody EnviarRespuestasRequest datos) {
        if (datos == null || datos.respuestas() == null || datos.respuestas().isEmpty()) {
            return ResponseEntity.badRequest().body("Debes responder el cuestionario.");
        }
        if (datos.respuestas().size() > MAXIMO_RESPUESTAS) {
            return ResponseEntity.badRequest().body("Se recibieron demasiadas respuestas.");
        }

        try {
            // Convertimos la lista de RespuestaDTO que llega del frontend
            // a un mapa (preguntaId -> opcionId), que es el formato que
            // espera el caso de uso.
            Map<Integer, Integer> mapaRespuestas = new HashMap<>();
            for (RespuestaDTO respuesta : datos.respuestas()) {
                if (respuesta == null) {
                    return ResponseEntity.badRequest().body("Hay respuestas vacías.");
                }
                mapaRespuestas.put(respuesta.preguntaId(), respuesta.opcionId());
            }

            Map<String, Integer> resultado =
                    casoUsoCalcularResultado.ejecutar(usuarioId, mapaRespuestas);

            return ResponseEntity.ok(resultado);

        } catch (CuestionarioFallidoException | ResultadoTestFallidoException excepcion) {
            return ResponseEntity.internalServerError().body(excepcion.getMessage());
        }
    }
}