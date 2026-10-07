package com.orientadorvocacional.repositorio.jpa;

import com.orientadorvocacional.entidad.CarreraEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarreraJpaRepository extends JpaRepository<CarreraEntity, Integer> {
}