package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.CuestionarioFallidoException;
import com.orientadorvocacional.modelo.Pregunta;
import com.orientadorvocacional.repositorio.IRepositorioPreguntas;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orquesta la obtención del cuestionario completo. Depende de la
 * interfaz IRepositorioPreguntas, no de la implementación concreta.
 */
@Service
public class CasoUsoObtenerCuestionario {

    private final IRepositorioPreguntas repositorioPreguntas;

    public CasoUsoObtenerCuestionario(IRepositorioPreguntas repositorioPreguntas) {
        this.repositorioPreguntas = repositorioPreguntas;
    }

    public List<Pregunta> ejecutar() throws CuestionarioFallidoException {
        return repositorioPreguntas.obtenerCuestionarioCompleto();
    }
}