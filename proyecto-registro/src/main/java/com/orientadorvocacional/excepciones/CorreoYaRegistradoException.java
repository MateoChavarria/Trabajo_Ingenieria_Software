package com.orientadorvocacional.excepciones;

/**
 * Se lanza cuando se intenta registrar un correo que ya existe
 * en el sistema de autenticacion de Supabase.
 */
public class CorreoYaRegistradoException extends Exception {

    public CorreoYaRegistradoException(String mensaje) {
        super(mensaje);
    }
}
