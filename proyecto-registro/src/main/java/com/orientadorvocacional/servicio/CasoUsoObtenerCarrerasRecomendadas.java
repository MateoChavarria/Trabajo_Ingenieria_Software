    package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.ConsultaCarrerasFallidaException;
import com.orientadorvocacional.modelo.AreasVocacionales;
import com.orientadorvocacional.modelo.Carrera;
import com.orientadorvocacional.modelo.CarreraRecomendada;
import com.orientadorvocacional.modelo.ResultadoRecomendacion;
import com.orientadorvocacional.repositorio.ICarreraRepositorio;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A partir del resultado del test (afinidad % por area, calculado en
 * la Historia 3), entrega el listado de carreras ordenadas de mayor
 * a menor afinidad.
 *
 * Como cada carrera pertenece a UNA sola area_categoria, su afinidad
 * es directamente la de esa area — por eso puede haber varias
 * carreras empatadas en el mismo porcentaje (por ejemplo, las 12
 * ingenierias comparten la afinidad de "ingenieria"). Es una
 * simplificacion deliberada para este sprint: no hay suficiente
 * informacion en el test para diferenciar DENTRO de una misma area
 * cual ingenieria especifica encaja mejor.
 */
@Service
public class CasoUsoObtenerCarrerasRecomendadas {

    /** Umbral bajo el cual consideramos que ninguna afinidad fue "fuerte". */
    private static final int UMBRAL_AFINIDAD_MINIMA = 40;

    /** Cuantas carreras como maximo se muestran, para no saturar la pantalla. */
    private static final int CANTIDAD_MAXIMA_RECOMENDACIONES = 5;

    private final ICarreraRepositorio repositorioCarreras;

    public CasoUsoObtenerCarrerasRecomendadas(ICarreraRepositorio repositorioCarreras) {
        this.repositorioCarreras = repositorioCarreras;
    }

    public ResultadoRecomendacion ejecutar(Map<String, Integer> afinidadPorArea)
            throws ConsultaCarrerasFallidaException {

        List<Carrera> todasLasCarreras = repositorioCarreras.obtenerTodas();

        List<CarreraRecomendada> recomendadas = new ArrayList<>();
        for (Carrera carrera : todasLasCarreras) {
            int afinidad = afinidadPorArea.getOrDefault(carrera.getAreaCategoria(), 0);
            recomendadas.add(new CarreraRecomendada(carrera, afinidad, construirMotivo(carrera, afinidad)));
        }

        recomendadas.sort((a, b) -> Integer.compare(b.getAfinidad(), a.getAfinidad()));

        List<CarreraRecomendada> topRecomendadas = recomendadas.size() > CANTIDAD_MAXIMA_RECOMENDACIONES
                ? recomendadas.subList(0, CANTIDAD_MAXIMA_RECOMENDACIONES)
                : recomendadas;

        // Tarea tecnica: "definir que pasa si ninguna carrera tiene
        // un puntaje suficientemente alto". Aqui no se oculta nada
        // ni se devuelve una lista vacia: se muestran igual las
        // mejores opciones disponibles, solo que el frontend va a
        // mostrar un aviso adicional gracias a esta bandera.
        boolean afinidadBaja = topRecomendadas.isEmpty()
                || topRecomendadas.get(0).getAfinidad() < UMBRAL_AFINIDAD_MINIMA;

        return new ResultadoRecomendacion(topRecomendadas, afinidadBaja);
    }

    private String construirMotivo(Carrera carrera, int afinidad) {
        String areaAmigable = AreasVocacionales.nombreAmigable(carrera.getAreaCategoria());
        return "Tu perfil mostró un " + afinidad + "% de afinidad con el área de " + areaAmigable + ".";
    }
}