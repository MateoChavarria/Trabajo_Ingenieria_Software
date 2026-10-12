package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.ConsultaUniversidadesFallidaException;
import com.orientadorvocacional.modelo.Universidad;
import com.orientadorvocacional.repositorio.IUniversidadRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Entrega el listado completo de universidades de Antioquia, sin filtrar.
 * Es público: lo usa la pantalla "Universidades", que no exige sesión.
 */
@Service
public class CasoUsoListarUniversidades {

    private final IUniversidadRepositorio repositorioUniversidades;

    public CasoUsoListarUniversidades(IUniversidadRepositorio repositorioUniversidades) {
        this.repositorioUniversidades = repositorioUniversidades;
    }

    public List<Universidad> ejecutar() throws ConsultaUniversidadesFallidaException {
        return repositorioUniversidades.obtenerTodas();
    }
}
