package com.orientadorvocacional.repositorio;

import com.orientadorvocacional.excepciones.CarreraNoEncontradaException;
import com.orientadorvocacional.excepciones.ConsultaCarrerasFallidaException;
import com.orientadorvocacional.modelo.Carrera;

import java.util.List;

public interface ICarreraRepositorio {

    List<Carrera> obtenerTodas() throws ConsultaCarrerasFallidaException;

    Carrera obtenerPorId(int id) throws CarreraNoEncontradaException, ConsultaCarrerasFallidaException;
}