package com.orientadorvocacional.repositorio;

import com.orientadorvocacional.excepciones.CuestionarioFallidoException;
import com.orientadorvocacional.modelo.Pregunta;

import java.util.List;

/**
 * Contrato para obtener el cuestionario del test vocacional.
 */
public interface IRepositorioPreguntas {

    List<Pregunta> obtenerCuestionarioCompleto() throws CuestionarioFallidoException;
}