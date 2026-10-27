package com.orientadorvocacional.repositorio.jpa;

import com.orientadorvocacional.entidad.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

/**
 * Al extender JpaRepository, Spring Data genera automaticamente la
 * implementacion de metodos como save(), findById(), existsById(),
 * deleteById(), etc. — no hay que escribir SQL ni una sola linea de
 * codigo aqui adentro.
 *
 * El primer parametro generico es la entidad (UsuarioEntity); el
 * segundo es el tipo de su clave primaria (UUID de Supabase Auth).
 */
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, java.util.UUID> {

    /** Lee solo el campo acceso_completo (vacio si el usuario no existe). */
    @Query("select u.accesoCompleto from UsuarioEntity u where u.id = :id")
    Optional<Boolean> consultarAccesoCompleto(@Param("id") UUID id);

    /** Marca el acceso completo. Devuelve cuantas filas cambio (0 si el usuario no existe). */
    @Modifying(clearAutomatically = true)
    @Query("update UsuarioEntity u set u.accesoCompleto = true where u.id = :id")
    int otorgarAccesoCompleto(@Param("id") UUID id);
}
