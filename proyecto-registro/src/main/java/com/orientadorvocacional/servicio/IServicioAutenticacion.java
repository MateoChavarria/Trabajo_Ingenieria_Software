package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.CorreoYaRegistradoException;
import com.orientadorvocacional.excepciones.CredencialesInvalidasException;
import com.orientadorvocacional.excepciones.InicioSesionFallidoException;
import com.orientadorvocacional.excepciones.RegistroFallidoException;
import com.orientadorvocacional.modelo.SesionUsuario;
import com.orientadorvocacional.modelo.Usuario;


/**
 * Contrato para cualquier proveedor de autenticacion.
 *
 * Al depender de esta interfaz (y no directamente de Supabase), el resto
 * del sistema queda protegido de cambios futuros: si algun dia cambian
 * de proveedor de autenticacion, solo se crea una nueva clase que
 * implemente esta interfaz, sin modificar el codigo que ya la usa.
 */
public interface IServicioAutenticacion {

    /**
     * Registra un nuevo usuario en el proveedor de autenticacion.
     *
     * @param usuario objeto ya validado (correo y contraseña correctos)
     * @return el identificador (id) que el proveedor asigna al usuario
     */
    String registrar(Usuario usuario)
            throws CorreoYaRegistradoException, RegistroFallidoException;
    
        /**
     * Inicia sesión con un correo y contraseña ya existentes.
     *
     * @param correo     correo del usuario
     * @param contrasena contraseña del usuario
     * @return la sesión activa, con su token y fecha de expiración
     */
    SesionUsuario iniciarSesion(String correo, String contrasena)
            throws CredencialesInvalidasException, InicioSesionFallidoException;
}
