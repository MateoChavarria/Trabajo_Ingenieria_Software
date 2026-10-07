package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.ResultadoTestFallidoException;
import com.orientadorvocacional.modelo.HistorialTestItem;
import com.orientadorvocacional.modelo.ResultadoTest;
import com.orientadorvocacional.repositorio.IRepositorioResultadoTest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Reutiliza CasoUsoObtenerPerfilVocacional para calcular, de cada
 * intento pasado, sus areas dominantes — asi no duplicamos la logica
 * de "cuales son las 3 mejores areas" en dos lugares distintos.
 */
@Service
public class CasoUsoObtenerHistorialTest {

    private final IRepositorioResultadoTest repositorioResultadoTest;
    private final CasoUsoObtenerPerfilVocacional casoUsoPerfil;

    public CasoUsoObtenerHistorialTest(IRepositorioResultadoTest repositorioResultadoTest,
                                        CasoUsoObtenerPerfilVocacional casoUsoPerfil) {
        this.repositorioResultadoTest = repositorioResultadoTest;
        this.casoUsoPerfil = casoUsoPerfil;
    }

    public List<HistorialTestItem> ejecutar(String usuarioId) throws ResultadoTestFallidoException {
        List<ResultadoTest> resultados = repositorioResultadoTest.obtenerPorUsuario(usuarioId);

        return resultados.stream()
                .map(resultado -> new HistorialTestItem(
                        resultado.getFecha(),
                        casoUsoPerfil.ejecutar(resultado.getDetallePorArea())))
                .toList();
    }
}