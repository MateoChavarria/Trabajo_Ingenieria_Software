package com.orientadorvocacional.repositorio;

import com.orientadorvocacional.excepciones.ResultadoTestFallidoException;
import com.orientadorvocacional.modelo.ResultadoTest;

import java.util.List;

public interface IRepositorioResultadoTest {

    void guardar(ResultadoTest resultado) throws ResultadoTestFallidoException;

    /** Historial completo de intentos de un usuario, del mas reciente al mas antiguo. */
    List<ResultadoTest> obtenerPorUsuario(String usuarioId) throws ResultadoTestFallidoException;
}