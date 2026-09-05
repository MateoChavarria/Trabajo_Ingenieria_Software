package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.CredencialesInvalidasException;
import com.orientadorvocacional.excepciones.InicioSesionFallidoException;
import com.orientadorvocacional.modelo.SesionUsuario;

/**
 * Orquesta el proceso de inicio de sesion. Depende de la interfaz
 * IServicioAutenticacion, no de la implementacion concreta de
 * Supabase (mismo principio de inversion de dependencias que usamos
 * en CasoUsoRegistrarUsuario).
 */
public class CasoUsoIniciarSesion {

    private final IServicioAutenticacion servicioAutenticacion;

    public CasoUsoIniciarSesion(IServicioAutenticacion servicioAutenticacion) {
        this.servicioAutenticacion = servicioAutenticacion;
    }

    public SesionUsuario ejecutar(String correo, String contrasena)
            throws CredencialesInvalidasException, InicioSesionFallidoException {

        if (correo == null || correo.isBlank()
                || contrasena == null || contrasena.isBlank()) {
            throw new CredencialesInvalidasException(
                    "Debes ingresar tu correo y tu contraseña.");
        }

        return servicioAutenticacion.iniciarSesion(correo, contrasena);
    }
}