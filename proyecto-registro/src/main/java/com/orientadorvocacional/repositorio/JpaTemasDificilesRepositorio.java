package com.orientadorvocacional.repositorio;

import com.orientadorvocacional.entidad.MateriaCarreraEntity;
import com.orientadorvocacional.entidad.SemestreCarreraEntity;
import com.orientadorvocacional.entidad.TemaDificilEntity;
import com.orientadorvocacional.excepciones.ConsultaTemasDificilesFallidaException;
import com.orientadorvocacional.repositorio.jpa.MateriaCarreraJpaRepository;
import com.orientadorvocacional.repositorio.jpa.SemestreCarreraJpaRepository;
import com.orientadorvocacional.repositorio.jpa.TemaDificilJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JpaTemasDificilesRepositorio implements ITemasDificilesRepositorio {

    private final SemestreCarreraJpaRepository semestreJpa;
    private final MateriaCarreraJpaRepository materiaJpa;
    private final TemaDificilJpaRepository temaJpa;

    public JpaTemasDificilesRepositorio(SemestreCarreraJpaRepository semestreJpa,
                                        MateriaCarreraJpaRepository materiaJpa,
                                        TemaDificilJpaRepository temaJpa) {
        this.semestreJpa = semestreJpa;
        this.materiaJpa = materiaJpa;
        this.temaJpa = temaJpa;
    }

    @Override
    public Map<Integer, Map<String, List<String>>> obtenerPorCarrera(int carreraId)
            throws ConsultaTemasDificilesFallidaException {
        try {
            // 1. Los semestres de la carrera, para saber el número de cada uno.
            Map<Integer, Integer> numeroPorIdSemestre = new HashMap<>();
            for (SemestreCarreraEntity semestre : semestreJpa.findByCarreraIdOrderByNumeroAsc(carreraId)) {
                numeroPorIdSemestre.put(semestre.getId(), semestre.getNumero());
            }
            if (numeroPorIdSemestre.isEmpty()) {
                return new HashMap<>();
            }

            // 2. Las materias de esos semestres, para saber a qué semestre y nombre pertenece cada id.
            Map<Integer, MateriaCarreraEntity> materiaPorId = new HashMap<>();
            for (MateriaCarreraEntity materia : materiaJpa.findBySemestreIdInOrderBySemestreIdAscIdAsc(
                    numeroPorIdSemestre.keySet())) {
                materiaPorId.put(materia.getId(), materia);
            }
            if (materiaPorId.isEmpty()) {
                return new HashMap<>();
            }

            // 3. Los temas de todas esas materias, en una sola consulta, ya ordenados.
            Map<Integer, Map<String, List<String>>> resultado = new HashMap<>();
            for (TemaDificilEntity tema : temaJpa.findByMateriaIdInOrderByMateriaIdAscOrdenAsc(
                    new ArrayList<>(materiaPorId.keySet()))) {
                MateriaCarreraEntity materia = materiaPorId.get(tema.getMateriaId());
                int numeroSemestre = numeroPorIdSemestre.get(materia.getSemestreId());
                resultado
                        .computeIfAbsent(numeroSemestre, clave -> new HashMap<>())
                        .computeIfAbsent(materia.getNombre(), clave -> new ArrayList<>())
                        .add(tema.getTema());
            }
            return resultado;

        } catch (Exception excepcion) {
            throw new ConsultaTemasDificilesFallidaException(
                    "No se pudieron consultar los temas más difíciles de la carrera.", excepcion);
        }
    }
}
