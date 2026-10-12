package com.orientadorvocacional.excepciones;

/**
 * Se lanza cuando se pide el pensum de una carrera que no pertenece a
 * una universidad con información completa (hoy, solo la UPB Medellín).
 */
public class PensumNoDisponibleException extends Exception {

    public PensumNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
