package com.orientadorvocacional.servicio;

import java.util.Map;

/**
 * Valida el mapa "afinidad por area" que llega desde el cliente antes
 * de usarlo, para rechazar datos manipulados o excesivamente grandes.
 */
public final class ValidadorAfinidad {

    static final int MAXIMO_AREAS = 50;
    static final int LONGITUD_MAXIMA_CODIGO_AREA = 50;

    private ValidadorAfinidad() {
    }

    public static void validar(Map<String, Integer> afinidadPorArea) {
        if (afinidadPorArea == null || afinidadPorArea.isEmpty()) {
            throw new IllegalArgumentException("La afinidad por área es obligatoria.");
        }
        if (afinidadPorArea.size() > MAXIMO_AREAS) {
            throw new IllegalArgumentException("Se recibieron demasiadas áreas.");
        }
        for (Map.Entry<String, Integer> entrada : afinidadPorArea.entrySet()) {
            String area = entrada.getKey();
            Integer valor = entrada.getValue();
            if (area == null || area.isBlank() || area.length() > LONGITUD_MAXIMA_CODIGO_AREA) {
                throw new IllegalArgumentException("Código de área no válido.");
            }
            if (valor == null || valor < 0 || valor > 100) {
                throw new IllegalArgumentException("La afinidad debe estar entre 0 y 100.");
            }
        }
    }
}