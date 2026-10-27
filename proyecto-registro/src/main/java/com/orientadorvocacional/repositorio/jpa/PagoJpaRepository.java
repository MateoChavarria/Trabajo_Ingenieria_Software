package com.orientadorvocacional.repositorio.jpa;

import com.orientadorvocacional.entidad.PagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PagoJpaRepository extends JpaRepository<PagoEntity, Long> {

    /** Busca un pago por su referencia, pero solo si pertenece a ese usuario. */
    Optional<PagoEntity> findByReferenciaAndUsuarioId(String referencia, UUID usuarioId);

    /** El pago pendiente mas reciente del usuario (si dejo alguno sin terminar). */
    Optional<PagoEntity> findFirstByUsuarioIdAndEstadoOrderByFechaDesc(UUID usuarioId, String estado);

    /**
     * Registra el resultado SOLO si el pago sigue pendiente. Es una sola
     * instruccion en la base de datos, asi que si dos peticiones llegan a
     * la vez, solo una gana (devuelve 1) y la otra recibe 0.
     */
    @Modifying(clearAutomatically = true)
    @Query("update PagoEntity p set p.estado = :estado, p.fechaResolucion = :fecha, p.motivoFallo = :motivo "
            + "where p.referencia = :referencia and p.estado = 'pendiente'")
    int resolverSiPendiente(@Param("referencia") String referencia,
                            @Param("estado") String estado,
                            @Param("fecha") LocalDateTime fecha,
                            @Param("motivo") String motivo);
}
