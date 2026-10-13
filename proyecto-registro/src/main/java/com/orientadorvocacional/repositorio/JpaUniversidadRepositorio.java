package com.orientadorvocacional.repositorio;

import com.orientadorvocacional.entidad.CarreraEntity;
import com.orientadorvocacional.entidad.UniversidadEntity;
import com.orientadorvocacional.excepciones.ConsultaUniversidadesFallidaException;
import com.orientadorvocacional.modelo.Universidad;
import com.orientadorvocacional.repositorio.jpa.CarreraJpaRepository;
import com.orientadorvocacional.repositorio.jpa.UniversidadJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class JpaUniversidadRepositorio implements IUniversidadRepositorio {

    private final UniversidadJpaRepository universidadJpa;
    private final CarreraJpaRepository carreraJpa;

    public JpaUniversidadRepositorio(UniversidadJpaRepository universidadJpa,
                                     CarreraJpaRepository carreraJpa) {
        this.universidadJpa = universidadJpa;
        this.carreraJpa = carreraJpa;
    }

    @Override
    public List<Universidad> obtenerTodas() throws ConsultaUniversidadesFallidaException {
        try {
            List<Universidad> universidades = new ArrayList<>();
            for (UniversidadEntity entidad : universidadJpa.findAllByOrderByNombreAsc()) {
                universidades.add(convertir(entidad));
            }
            return universidades;

        } catch (Exception excepcion) {
            throw new ConsultaUniversidadesFallidaException(
                    "No se pudo consultar el listado de universidades.", excepcion);
        }
    }

    @Override
    public boolean carreraPerteneceAUniversidadConInformacionCompleta(int carreraId)
            throws ConsultaUniversidadesFallidaException {
        try {
            CarreraEntity carrera = carreraJpa.findById(carreraId).orElse(null);
            if (carrera == null || carrera.getUniversidadId() == null) {
                return false;
            }
            return universidadJpa.findById(carrera.getUniversidadId())
                    .map(UniversidadEntity::isTieneInformacionCompleta)
                    .orElse(false);

        } catch (Exception excepcion) {
            throw new ConsultaUniversidadesFallidaException(
                    "No se pudo verificar la universidad de la carrera.", excepcion);
        }
    }

    private Universidad convertir(UniversidadEntity entidad) {
        return new Universidad(
                entidad.getId(),
                entidad.getNombre(),
                entidad.getCaracter(),
                entidad.getMunicipio(),
                entidad.isTieneInformacionCompleta()
        );
    }
}
