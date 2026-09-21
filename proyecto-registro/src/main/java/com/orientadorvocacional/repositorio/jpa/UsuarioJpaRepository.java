package com.orientadorvocacional.repositorio.jpa;

import com.orientadorvocacional.entidad.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Al extender JpaRepository, Spring Data genera automaticamente la
 * implementacion de metodos como save(), findById(), existsById(),
 * deleteById(), etc. — no hay que escribir SQL ni una sola linea de
 * codigo aqui adentro.
 *
 * El primer parametro generico es la entidad (UsuarioEntity); el
 * segundo es el tipo de su clave primaria (String, porque el id es
 * un UUID de Supabase Auth representado como texto).
 */
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, java.util.UUID> {
}
