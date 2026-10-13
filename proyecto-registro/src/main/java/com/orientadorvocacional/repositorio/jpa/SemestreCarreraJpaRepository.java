package com.orientadorvocacional.repositorio.jpa;

import com.orientadorvocacional.entidad.SemestreCarreraEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SemestreCarreraJpaRepository extends JpaRepository<SemestreCarreraEntity, Integer> {

    /** Los semestres de una carrera, del 1 en adelante. */
    List<SemestreCarreraEntity> findByCarreraIdOrderByNumeroAsc(Integer carreraId);
}
