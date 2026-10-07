package com.orientadorvocacional.repositorio.jpa;

import com.orientadorvocacional.entidad.ResultadoTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ResultadoTestJpaRepository extends JpaRepository<ResultadoTestEntity, Long> {
    List<ResultadoTestEntity> findByUsuarioIdOrderByFechaDesc(UUID usuarioId);
}