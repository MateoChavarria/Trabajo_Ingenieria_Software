package com.orientadorvocacional.repositorio;

import com.orientadorvocacional.excepciones.OperacionPagoFallidaException;
import com.orientadorvocacional.repositorio.jpa.UsuarioJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** Implementacion de IRepositorioAcceso sobre la tabla "usuarios" (JPA). */
@Repository
public class JpaRepositorioAcceso implements IRepositorioAcceso {

    private static final long DIEZ_MINUTOS_MS = 10 * 60 * 1000L;

    private final UsuarioJpaRepository usuarioJpa;
    // Solo se guarda el "si tiene acceso": quien paga no lo pierde. El "no" siempre se
    // consulta, asi que tras un pago el acceso se reconoce de inmediato.
    private final CacheTemporal<String, Boolean> accesosConfirmados = new CacheTemporal<>(DIEZ_MINUTOS_MS, 10_000);

    public JpaRepositorioAcceso(UsuarioJpaRepository usuarioJpa) {
        this.usuarioJpa = usuarioJpa;
    }

    @Override
    public boolean tieneAccesoCompleto(String usuarioId) throws OperacionPagoFallidaException {
        try {
            if (accesosConfirmados.consultar(usuarioId) != null) {
                return true;
            }
            boolean acceso = usuarioJpa.consultarAccesoCompleto(UUID.fromString(usuarioId)).orElse(false);
            if (acceso) {
                accesosConfirmados.guardar(usuarioId, Boolean.TRUE);
            }
            return acceso;
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
        accesosConfirmados.guardar(usuarioId, Boolean.TRUE);
    }
}
