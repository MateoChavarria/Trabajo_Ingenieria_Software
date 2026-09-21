package com.orientadorvocacional.repositorio.jpa;

import com.orientadorvocacional.entidad.PreguntaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreguntaJpaRepository extends JpaRepository<PreguntaEntity, Integer> {

    /**
     * Metodo "derivado": Spring Data JPA lee el nombre del metodo
     * (findAllByOrderByIdAsc) y genera solo, sin que escribamos SQL,
     * la consulta "SELECT * FROM preguntas ORDER BY id ASC".
     *
     * Esto reemplaza al "&order=id.asc" que antes agregabamos a mano
     * en la URL cuando consultabamos por PostgREST.
     */
    List<PreguntaEntity> findAllByOrderByIdAsc();
}
