package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.OperacionPagoFallidaException;
import com.orientadorvocacional.repositorio.IRepositorioAcceso;

import java.util.HashSet;
import java.util.Set;

/** Repositorio de acceso "falso" para las pruebas: guarda en un conjunto los usuarios con acceso completo. */
class RepositorioAccesoEnMemoria implements IRepositorioAcceso {

    private final Set<String> conAcceso = new HashSet<>();

    /** Simula una caida de la base de datos. */
    boolean baseDeDatosCaida = false;

    void darAcceso(String usuarioId) {
        conAcceso.add(usuarioId);
    }

    @Override
    public boolean tieneAccesoCompleto(String usuarioId) throws OperacionPagoFallidaException {
        if (baseDeDatosCaida) {
            throw new OperacionPagoFallidaException("base caida");
        }
        return conAcceso.contains(usuarioId);
    }

    @Override
    public void otorgarAccesoCompleto(String usuarioId) throws OperacionPagoFallidaException {
        if (baseDeDatosCaida) {
            throw new OperacionPagoFallidaException("base caida");
        }
        conAcceso.add(usuarioId);
    }
}
