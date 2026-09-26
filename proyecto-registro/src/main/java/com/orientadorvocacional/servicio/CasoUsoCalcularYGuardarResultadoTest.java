package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.CuestionarioFallidoException;
import com.orientadorvocacional.excepciones.ResultadoTestFallidoException;
import com.orientadorvocacional.modelo.OpcionRespuesta;
import com.orientadorvocacional.modelo.Pregunta;
import com.orientadorvocacional.modelo.ResultadoTest;
import com.orientadorvocacional.repositorio.IRepositorioPreguntas;
import com.orientadorvocacional.repositorio.IRepositorioResultadoTest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Orquesta el calculo del resultado del test vocacional:
 *   1) trae de nuevo el cuestionario completo (el servidor nunca
 *      confia en pesos que le mande el navegador),
 *   2) calcula el puntaje CRUDO obtenido por area (suma de pesos de
 *      las opciones elegidas),
 *   3) calcula el puntaje MAXIMO posible por area (si el usuario
 *      hubiera elegido, en cada pregunta, la opcion que mas suma a
 *      esa area),
 *   4) normaliza: afinidad% = obtenido / maximo * 100 — esto es lo
 *      que permite comparar areas entre si de forma justa, sin
 *      importar cuantas preguntas toque cada una,
 *   5) guarda el resultado (ya en porcentajes) asociado al usuario.
 *
 * Esta afinidad por area es, ademas, la misma que se va a usar para
 * la "afinidad a una carrera" en la Historia 5: cada carrera se va a
 * asociar a UNA de estas areas, y su afinidad sera directamente la
 * de esa area — no hace falta un calculo distinto.
 */
@Service
public class CasoUsoCalcularYGuardarResultadoTest {

    private final IRepositorioPreguntas repositorioPreguntas;
    private final IRepositorioResultadoTest repositorioResultadoTest;

    public CasoUsoCalcularYGuardarResultadoTest(IRepositorioPreguntas repositorioPreguntas,
                                                 IRepositorioResultadoTest repositorioResultadoTest) {
        this.repositorioPreguntas = repositorioPreguntas;
        this.repositorioResultadoTest = repositorioResultadoTest;
    }

    public Map<String, Integer> ejecutar(String usuarioId, Map<Integer, Integer> respuestasSeleccionadas)
            throws CuestionarioFallidoException, ResultadoTestFallidoException {

        var preguntas = repositorioPreguntas.obtenerCuestionarioCompleto();

        Map<String, Integer> puntajeObtenidoPorArea = calcularPuntajeObtenido(preguntas, respuestasSeleccionadas);
        Map<String, Integer> puntajeMaximoPorArea = calcularPuntajeMaximoPosible(preguntas);
        Map<String, Integer> afinidadPorArea = normalizarComoPocentaje(puntajeObtenidoPorArea, puntajeMaximoPorArea);

        ResultadoTest resultado = new ResultadoTest(usuarioId, afinidadPorArea);
        repositorioResultadoTest.guardar(resultado);

        return afinidadPorArea;
    }

    /**
     * Suma, por area, los pesos de las opciones que el usuario
     * realmente eligio.
     */
    private Map<String, Integer> calcularPuntajeObtenido(Iterable<Pregunta> preguntas,
                                                          Map<Integer, Integer> respuestasSeleccionadas) {
        Map<String, Integer> puntajePorArea = new HashMap<>();

        for (Pregunta pregunta : preguntas) {
            Integer idOpcionElegida = respuestasSeleccionadas.get(pregunta.getId());
            if (idOpcionElegida == null) {
                continue; // pregunta no respondida
            }

            for (OpcionRespuesta opcion : pregunta.getOpciones()) {
                if (opcion.getId() == idOpcionElegida) {
                    puntajePorArea.merge(opcion.getArea(), opcion.getPeso(), Integer::sum);
                    break;
                }
            }
        }

        return puntajePorArea;
    }

    /**
     * Para cada pregunta, encuentra el peso mas alto que cada area
     * podria haber recibido en esa pregunta (la mejor opcion posible
     * para esa area), y lo suma a lo largo de todas las preguntas.
     * Este es el "techo" contra el que se compara lo que el usuario
     * obtuvo de verdad.
     */
    private Map<String, Integer> calcularPuntajeMaximoPosible(Iterable<Pregunta> preguntas) {
        Map<String, Integer> maximoPorArea = new HashMap<>();

        for (Pregunta pregunta : preguntas) {
            Map<String, Integer> maximoEnEstaPregunta = new HashMap<>();

            for (OpcionRespuesta opcion : pregunta.getOpciones()) {
                maximoEnEstaPregunta.merge(opcion.getArea(), opcion.getPeso(), Math::max);
            }

            maximoEnEstaPregunta.forEach((area, peso) -> maximoPorArea.merge(area, peso, Integer::sum));
        }

        return maximoPorArea;
    }

    /**
     * afinidad% = obtenido / maximo * 100, redondeado. Si un area no
     * tiene maximo (no deberia pasar, pero por seguridad), su
     * afinidad queda en 0 en vez de dividir por cero.
     */
    private Map<String, Integer> normalizarComoPocentaje(Map<String, Integer> obtenidoPorArea,
                                                          Map<String, Integer> maximoPorArea) {
        Map<String, Integer> afinidadPorArea = new HashMap<>();

        Set<String> todasLasAreas = new HashSet<>();
        todasLasAreas.addAll(obtenidoPorArea.keySet());
        todasLasAreas.addAll(maximoPorArea.keySet());

        for (String area : todasLasAreas) {
            int obtenido = obtenidoPorArea.getOrDefault(area, 0);
            int maximo = maximoPorArea.getOrDefault(area, 0);

            int porcentaje = (maximo == 0) ? 0 : (int) Math.round((obtenido * 100.0) / maximo);
            afinidadPorArea.put(area, porcentaje);
        }

        return afinidadPorArea;
    }
}