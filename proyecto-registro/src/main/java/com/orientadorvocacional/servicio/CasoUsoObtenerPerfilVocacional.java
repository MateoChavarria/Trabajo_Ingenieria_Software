package com.orientadorvocacional.servicio;

import com.orientadorvocacional.modelo.AreaDominante;
import com.orientadorvocacional.modelo.AreasVocacionales;
import com.orientadorvocacional.modelo.PerfilVocacional;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * A partir de la afinidad por area ya calculada en la Historia 3
 * (CasoUsoCalcularYGuardarResultadoTest), deriva el perfil del
 * usuario: sus 3 areas dominantes, cada una con su texto
 * interpretativo.
 *
 * No hace ninguna consulta a la base de datos: toda la informacion
 * que necesita ya viene en el parametro "afinidadPorArea", que es
 * el mismo mapa que devuelve /api/test/resultado. Por eso no
 * necesita ningun repositorio inyectado.
 */
@Service
public class CasoUsoObtenerPerfilVocacional {

    /**
     * Regla para decidir las "areas dominantes": las 3 categorias
     * con mayor puntaje. En caso de empate, se desempata por orden
     * alfabetico del codigo del area, para que el resultado sea
     * siempre el mismo ante los mismos datos (determinista).
     */
    private static final int CANTIDAD_AREAS_DOMINANTES = 3;

    public PerfilVocacional ejecutar(Map<String, Integer> afinidadPorArea) {
        List<AreaDominante> areasDominantes = afinidadPorArea.entrySet().stream()
                .sorted(Comparator
                        .<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue).reversed()
                        .thenComparing(Map.Entry::getKey))
                .limit(CANTIDAD_AREAS_DOMINANTES)
                .map(entrada -> new AreaDominante(
                        entrada.getKey(),
                        AreasVocacionales.nombreAmigable(entrada.getKey()),
                        entrada.getValue(),
                        AreasVocacionales.interpretacion(entrada.getKey())
                ))
                .toList();

        return new PerfilVocacional(areasDominantes, afinidadPorArea);
    }
}