package com.orientadorvocacional.modelo;

import com.orientadorvocacional.excepciones.ContrasenaInvalidaException;
import com.orientadorvocacional.excepciones.CorreoInvalidoException;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * Representa a un estudiante que se registra en la plataforma.
 *
 * Todos los campos son privados (encapsulamiento): nadie fuera de esta
 * clase puede modificarlos directamente, solo a traves de los metodos
 * publicos (setters), que son los unicos responsables de validar los
 * datos antes de aceptarlos.
 *
 * El constructor NO valida nada por si mismo: su unica responsabilidad
 * es orquestar la creacion del objeto, delegando la validacion a los
 * setters. Asi, la logica de validacion vive en un solo lugar y no se
 * duplica ni se le asigna al constructor una responsabilidad que no le
 * corresponde (principio de responsabilidad unica).
 */
public class Usuario {

    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private String correo;
    private String contrasena;
    private final LocalDateTime fechaRegistro;
    private EstadoCuenta estadoCuenta;

    public Usuario(String correo, String contrasena)
            throws CorreoInvalidoException, ContrasenaInvalidaException {
        setCorreo(correo);
        setContrasena(contrasena);
        this.fechaRegistro = LocalDateTime.now();
        this.estadoCuenta = EstadoCuenta.ACTIVO;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) throws CorreoInvalidoException {
        if (correo == null || correo.isBlank()) {
            throw new CorreoInvalidoException("El correo no puede estar vacío.");
        }
        String correoNormalizado = correo.trim().toLowerCase();
        if (!PATRON_CORREO.matcher(correoNormalizado).matches()) {
            throw new CorreoInvalidoException("El correo no tiene un formato válido.");
        }
        this.correo = correoNormalizado;
    }

    /**
     * ADVERTENCIA: este metodo existe unicamente para que el servicio de
     * autenticacion pueda leer la contrasena y enviarla, una sola vez y
     * por HTTPS, a Supabase (quien se encarga de cifrarla y almacenarla).
     * Este valor nunca debe registrarse en logs, guardarse en la tabla
     * "usuarios" propia, ni exponerse por ningun otro medio.
     */
    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) throws ContrasenaInvalidaException {
        if (contrasena == null || contrasena.length() < 8) {
            throw new ContrasenaInvalidaException(
                    "La contraseña debe tener al menos 8 caracteres.");
        }
        if (contrasena.chars().noneMatch(Character::isUpperCase)) {
            throw new ContrasenaInvalidaException(
                    "La contraseña debe incluir al menos una letra mayúscula.");
        }
        if (contrasena.chars().noneMatch(Character::isDigit)) {
            throw new ContrasenaInvalidaException(
                    "La contraseña debe incluir al menos un número.");
        }
        this.contrasena = contrasena;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public EstadoCuenta getEstadoCuenta() {
        return estadoCuenta;
    }

    public void setEstadoCuenta(EstadoCuenta estadoCuenta) {
        if (estadoCuenta == null) {
            throw new IllegalArgumentException("El estado de la cuenta no puede ser nulo.");
        }
        this.estadoCuenta = estadoCuenta;
    }
}
