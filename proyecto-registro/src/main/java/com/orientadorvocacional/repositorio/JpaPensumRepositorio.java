package com.orientadorvocacional.repositorio;

import com.orientadorvocacional.entidad.MateriaCarreraEntity;
import com.orientadorvocacional.entidad.SemestreCarreraEntity;
import com.orientadorvocacional.excepciones.ConsultaPensumFallidaException;
import com.orientadorvocacional.modelo.Carrera;
import com.orientadorvocacional.modelo.MateriaPensum;
import com.orientadorvocacional.modelo.PensumCarrera;
import com.orientadorvocacional.modelo.SemestrePensum;
import com.orientadorvocacional.repositorio.jpa.MateriaCarreraJpaRepository;
import com.orientadorvocacional.repositorio.jpa.SemestreCarreraJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JpaPensumRepositorio implements IPensumRepositorio {

    private final SemestreCarreraJpaRepository semestreJpa;
    private final MateriaCarreraJpaRepository materiaJpa;

    public JpaPensumRepositorio(SemestreCarreraJpaRepository semestreJpa,
                                MateriaCarreraJpaRepository materiaJpa) {
        this.semestreJpa = semestreJpa;
        this.materiaJpa = materiaJpa;
    }

    @Override
    public PensumCarrera obtenerPorCarrera(Carrera carrera) throws ConsultaPensumFallidaException {
        try {
            // 1. Los semestres de la carrera, ya ordenados (1, 2, 3...).
            List<SemestreCarreraEntity> semestres = semestreJpa.findByCarreraIdOrderByNumeroAsc(carrera.getId());

            // 2. Las materias de todos esos semestres, en una sola consulta.
            List<Integer> idsSemestres = new ArrayList<>();
            for (SemestreCarreraEntity semestre : semestres) {
                idsSemestres.add(semestre.getId());
            }
            Map<Integer, List<MateriaPensum>> materiasPorSemestre = agruparMateriasPorSemestre(idsSemestres);

            // 3. Se arma el pensum: cada semestre con su lista de materias.
            List<SemestrePensum> semestresPensum = new ArrayList<>();
            for (SemestreCarreraEntity semestre : semestres) {
                List<MateriaPensum> materias = materiasPorSemestre.getOrDefault(semestre.getId(), new ArrayList<>());
                semestresPensum.add(new SemestrePensum(
                        semestre.getNumero(),
                        semestre.getEnfoquePrincipal(),
                        semestre.getDescripcion(),
                        materias
                ));
            }
            return new PensumCarrera(carrera.getId(), carrera.getNombre(), semestresPensum);

        } catch (Exception excepcion) {
            throw new ConsultaPensumFallidaException(
                    "No se pudo consultar el plan de estudios de la carrera.", excepcion);
        }
    }

    /** Consulta las materias de los semestres dados y las deja agrupadas por id de semestre. */
    private Map<Integer, List<MateriaPensum>> agruparMateriasPorSemestre(List<Integer> idsSemestres) {
        Map<Integer, List<MateriaPensum>> materiasPorSemestre = new HashMap<>();
        if (idsSemestres.isEmpty()) {
            return materiasPorSemestre;
        }
        for (MateriaCarreraEntity materia : materiaJpa.findBySemestreIdInOrderBySemestreIdAscIdAsc(idsSemestres)) {
            materiasPorSemestre
                    .computeIfAbsent(materia.getSemestreId(), clave -> new ArrayList<>())
                    .add(new MateriaPensum(materia.getNombre(), materia.getCreditos(), materia.getEnfoqueTematico()));
        }
        return materiasPorSemestre;
    }
}
