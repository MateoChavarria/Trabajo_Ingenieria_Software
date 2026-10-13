package com.orientadorvocacional.repositorio.jpa;

import com.orientadorvocacional.entidad.MateriaCarreraEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface MateriaCarreraJpaRepository extends JpaRepository<MateriaCarreraEntity, Integer> {

    /** Las materias de varios semestres a la vez, en el orden en que se cargaron. */
    List<MateriaCarreraEntity> findBySemestreIdInOrderBySemestreIdAscIdAsc(Collection<Integer> semestreIds);
}
