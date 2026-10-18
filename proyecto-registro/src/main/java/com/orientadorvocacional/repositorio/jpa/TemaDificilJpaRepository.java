package com.orientadorvocacional.repositorio.jpa;

import com.orientadorvocacional.entidad.TemaDificilEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TemaDificilJpaRepository extends JpaRepository<TemaDificilEntity, Integer> {

    /** Los temas de varias materias a la vez, cada materia con sus temas en su orden (1, 2, 3). */
    List<TemaDificilEntity> findByMateriaIdInOrderByMateriaIdAscOrdenAsc(Collection<Integer> materiaIds);
}
