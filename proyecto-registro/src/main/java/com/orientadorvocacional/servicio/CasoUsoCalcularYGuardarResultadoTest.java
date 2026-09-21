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
import java.util.Map;

/**
 * Orquesta el calculo del resultado del test vocacional:
 *   1) trae de nuevo el cuestionario completo (para saber el area y
 *      el peso de cada opcion que el usuario marco),
 *   2) suma los pesos de las opciones elegidas, agrupados por area,
 *   3) guarda el resultado asociado al usuario.
 *
 * Tarea tecnica de esta semana: "Guardar el resultado del test
 * asociado al usuario".
 *
 * Se vuelve a consultar el cuestionario en el servidor (en vez de
 * confiar en lo que mande el navegador) a proposito: si solo
 * confiaramos en los pesos que nos manda el frontend, alguien podria
 * manipular la peticion HTTP y "tunear" su resultado. Aqui el
 * servidor es la unica fuente de verdad para los pesos.
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

    /**
     * @param usuarioId             id del usuario que realizo el test
     * @param respuestasSeleccionadas mapa de "id de pregunta" -> "id de
     *                                opcion elegida", tal como lo arma
     *                                el frontend mientras el usuario
     *                                responde
     * @return el detalle del puntaje por area, para mostrarselo de una
     *         vez al usuario en la misma respuesta
     */
    public Map<String, Integer> ejecutar(String usuarioId, Map<Integer, Integer> respuestasSeleccionadas)
            throws CuestionarioFallidoException, ResultadoTestFallidoException {

        Map<String, Integer> puntajePorArea = calcularPuntajePorArea(respuestasSeleccionadas);

        ResultadoTest resultado = new ResultadoTest(usuarioId, puntajePorArea);
        repositorioResultadoTest.guardar(resultado);

        return puntajePorArea;
    }

    private Map<String, Integer> calcularPuntajePorArea(Map<Integer, Integer> respuestasSeleccionadas)
            throws CuestionarioFallidoException {

        var preguntas = repositorioPreguntas.obtenerCuestionarioCompleto();
        Map<String, Integer> puntajePorArea = new HashMap<>();

        for (Pregunta pregunta : preguntas) {
            Integer idOpcionElegida = respuestasSeleccionadas.get(pregunta.getId());

            // Si el usuario no respondio esta pregunta, simplemente no
            // suma nada (no deberia pasar si el frontend valida bien
            // antes de enviar, pero el backend no confia ciegamente).
            if (idOpcionElegida == null) {
                continue;
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
}
