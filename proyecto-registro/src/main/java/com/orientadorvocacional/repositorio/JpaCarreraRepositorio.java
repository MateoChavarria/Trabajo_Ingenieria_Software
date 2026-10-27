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

    private static final long CINCO_MINUTOS_MS = 5 * 60 * 1000L;

    private final CarreraJpaRepository repositorioJpa;
    private final CacheTemporal<String, List<Carrera>> cacheListado = new CacheTemporal<>(CINCO_MINUTOS_MS, 1);
    private final CacheTemporal<Integer, Carrera> cachePorId = new CacheTemporal<>(CINCO_MINUTOS_MS, 500);

    public JpaCarreraRepositorio(CarreraJpaRepository repositorioJpa) {
        this.repositorioJpa = repositorioJpa;
    }

    @Override
    public List<Carrera> obtenerTodas() throws ConsultaCarrerasFallidaException {
        try {
            return cacheListado.obtener("todas", () -> {
                List<Carrera> carreras = new ArrayList<>();
                for (CarreraEntity entidad : repositorioJpa.findAll()) {
                    carreras.add(convertir(entidad));
                }
                return carreras;
            });

        } catch (Exception excepcion) {
            throw new ConsultaCarrerasFallidaException(
                    "No se pudo consultar el listado de carreras.", excepcion);
        }
    }

    @Override
    public Carrera obtenerPorId(int id) throws CarreraNoEncontradaException, ConsultaCarrerasFallidaException {
        try {
            Carrera guardada = cachePorId.consultar(id);
            if (guardada != null) {
                return guardada;
            }
            CarreraEntity entidad = repositorioJpa.findById(id)
                    .orElseThrow(() -> new CarreraNoEncontradaException(
                            "No existe una carrera con id " + id + "."));
            Carrera carrera = convertir(entidad);
            cachePorId.guardar(id, carrera);
            return carrera;

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