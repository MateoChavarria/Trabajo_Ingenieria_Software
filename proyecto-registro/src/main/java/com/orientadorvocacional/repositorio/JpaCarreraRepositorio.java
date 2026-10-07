package com.orientadorvocacional.repositorio;

import com.orientadorvocacional.entidad.CarreraEntity;
import com.orientadorvocacional.excepciones.CarreraNoEncontradaException;
import com.orientadorvocacional.excepciones.ConsultaCarrerasFallidaException;
import com.orientadorvocacional.modelo.Carrera;
import com.orientadorvocacional.repositorio.jpa.CarreraJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class JpaCarreraRepositorio implements ICarreraRepositorio {

    private final CarreraJpaRepository repositorioJpa;

    public JpaCarreraRepositorio(CarreraJpaRepository repositorioJpa) {
        this.repositorioJpa = repositorioJpa;
    }

    @Override
    public List<Carrera> obtenerTodas() throws ConsultaCarrerasFallidaException {
        try {
            List<Carrera> carreras = new ArrayList<>();
            for (CarreraEntity entidad : repositorioJpa.findAll()) {
                carreras.add(convertir(entidad));
            }
            return carreras;

        } catch (Exception excepcion) {
            throw new ConsultaCarrerasFallidaException(
                    "No se pudo consultar el listado de carreras.", excepcion);
        }
    }

    @Override
    public Carrera obtenerPorId(int id) throws CarreraNoEncontradaException, ConsultaCarrerasFallidaException {
        try {
            CarreraEntity entidad = repositorioJpa.findById(id)
                    .orElseThrow(() -> new CarreraNoEncontradaException(
                            "No existe una carrera con id " + id + "."));
            return convertir(entidad);

        } catch (CarreraNoEncontradaException excepcion) {
            throw excepcion;
        } catch (Exception excepcion) {
            throw new ConsultaCarrerasFallidaException("No se pudo consultar la carrera.", excepcion);
        }
    }

    private Carrera convertir(CarreraEntity entidad) {
        return new Carrera(
                entidad.getId(),
                entidad.getNombre(),
                entidad.getDescripcion(),
                entidad.getCampoLaboral(),
                entidad.getAreaCategoria(),
                entidad.getDuracionSemestres(),
                entidad.getTarifaSemestre()
        );
    }
}