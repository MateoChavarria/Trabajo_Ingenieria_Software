package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.ConsultaCarrerasFallidaException;
import com.orientadorvocacional.modelo.Carrera;
import com.orientadorvocacional.repositorio.ICarreraRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Lista TODAS las carreras, sin filtrar por afinidad. Es lo que usa
 * la pantalla publica de "Explorar carreras", que no requiere que el
 * usuario haya hecho el test ni haya iniciado sesion.
 */
@Service
public class CasoUsoListarTodasLasCarreras {

    private final ICarreraRepositorio repositorioCarreras;

    public CasoUsoListarTodasLasCarreras(ICarreraRepositorio repositorioCarreras) {
        this.repositorioCarreras = repositorioCarreras;
    }

    public List<Carrera> ejecutar() throws ConsultaCarrerasFallidaException {
        return repositorioCarreras.obtenerTodas();
    }
}