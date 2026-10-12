package com.orientadorvocacional.repositorio.jpa;

import com.orientadorvocacional.entidad.UniversidadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UniversidadJpaRepository extends JpaRepository<UniversidadEntity, Integer> {

    /** Todas las universidades, en orden alfabético por nombre. */
    List<UniversidadEntity> findAllByOrderByNombreAsc();
}
