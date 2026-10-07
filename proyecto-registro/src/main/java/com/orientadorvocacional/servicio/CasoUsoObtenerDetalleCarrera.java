package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.CarreraNoEncontradaException;
import com.orientadorvocacional.excepciones.ConsultaCarrerasFallidaException;
import com.orientadorvocacional.modelo.Carrera;
import com.orientadorvocacional.repositorio.ICarreraRepositorio;
import org.springframework.stereotype.Service;

@Service
public class CasoUsoObtenerDetalleCarrera {

    private final ICarreraRepositorio repositorioCarreras;

    public CasoUsoObtenerDetalleCarrera(ICarreraRepositorio repositorioCarreras) {
        this.repositorioCarreras = repositorioCarreras;
    }

    public Carrera ejecutar(int id) throws CarreraNoEncontradaException, ConsultaCarrerasFallidaException {
        return repositorioCarreras.obtenerPorId(id);
    }
}