package com.orientadorvocacional.repositorio.jpa;

import com.orientadorvocacional.entidad.PreguntaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PreguntaJpaRepository extends JpaRepository<PreguntaEntity, Integer> {

    /**
     * "JOIN FETCH" le dice a Hibernate que traiga las preguntas Y sus
     * opciones en UNA sola consulta SQL (con un JOIN real), en vez de
     * una consulta aparte por cada pregunta (el problema N+1 que
     * causaba los 12 segundos de carga). "DISTINCT" evita filas
     * repetidas por el join cuando una pregunta tiene varias opciones.
     */
    @Query("SELECT DISTINCT p FROM PreguntaEntity p LEFT JOIN FETCH p.opciones ORDER BY p.id ASC")
    List<PreguntaEntity> findAllConOpcionesOrderByIdAsc();
}