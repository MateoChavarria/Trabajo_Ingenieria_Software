package com.orientadorvocacional.repositorio;

import com.orientadorvocacional.excepciones.ResultadoTestFallidoException;
import com.orientadorvocacional.modelo.ResultadoTest;

/**
 * Contrato para guardar el resultado de un test vocacional. Se separa
 * de IRepositorioUsuario e IRepositorioPreguntas porque es una
 * responsabilidad distinta (principio de segregacion de interfaces),
 * aunque las tres terminen usando JPA por dentro.
 */
public interface IRepositorioResultadoTest {

    void guardar(ResultadoTest resultado) throws ResultadoTestFallidoException;
}
