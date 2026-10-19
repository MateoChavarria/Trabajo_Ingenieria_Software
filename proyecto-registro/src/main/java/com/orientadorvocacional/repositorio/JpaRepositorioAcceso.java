package com.orientadorvocacional.repositorio;

import com.orientadorvocacional.excepciones.OperacionPagoFallidaException;
import com.orientadorvocacional.repositorio.jpa.UsuarioJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** Implementacion de IRepositorioAcceso sobre la tabla "usuarios" (JPA). */
@Repository
public class JpaRepositorioAcceso implements IRepositorioAcceso {

    private final UsuarioJpaRepository usuarioJpa;

    public JpaRepositorioAcceso(UsuarioJpaRepository usuarioJpa) {
        this.usuarioJpa = usuarioJpa;
    }

    @Override
    public boolean tieneAccesoCompleto(String usuarioId) throws OperacionPagoFallidaException {
        try {
            return usuarioJpa.consultarAccesoCompleto(UUID.fromString(usuarioId)).orElse(false);
        } catch (Exception excepcion) {
            throw new OperacionPagoFallidaException(
                    "No se pudo verificar tu acceso. Intenta más tarde.", excepcion);
        }
    }

    @Override
    @Transactional
    public void otorgarAccesoCompleto(String usuarioId) throws OperacionPagoFallidaException {
        int filasCambiadas;
        try {
            filasCambiadas = usuarioJpa.otorgarAccesoCompleto(UUID.fromString(usuarioId));
        } catch (Exception excepcion) {
            throw new OperacionPagoFallidaException(
                    "No se pudo activar tu acceso completo.", excepcion);
        }
        if (filasCambiadas == 0) {
            throw new OperacionPagoFallidaException("No se encontró tu perfil de usuario.");
        }
    }
}
