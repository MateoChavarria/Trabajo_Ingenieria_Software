package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.ContrasenaInvalidaException;
import com.orientadorvocacional.excepciones.CorreoInvalidoException;
import com.orientadorvocacional.excepciones.CorreoYaRegistradoException;
import com.orientadorvocacional.excepciones.RegistroFallidoException;
import com.orientadorvocacional.modelo.Usuario;
import com.orientadorvocacional.repositorio.IRepositorioUsuario;

/**
 * Orquesta el proceso completo de registro:
 *   1) crea y valida el Usuario (a traves de sus setters)
 *   2) lo registra en el proveedor de autenticacion
 *   3) guarda su perfil en la tabla "usuarios"
 *
 * Esta clase depende de las INTERFACES (IServicioAutenticacion,
 * IRepositorioUsuario) y no de las implementaciones concretas de
 * Supabase. Esto es el principio de inversion de dependencias:
 * si mañana cambian de proveedor, esta clase no se toca, solo se
 * le inyecta una implementacion distinta desde donde se construye.
 */
public class CasoUsoRegistrarUsuario {

    private final IServicioAutenticacion servicioAutenticacion;
    private final IRepositorioUsuario repositorioUsuario;

    public CasoUsoRegistrarUsuario(IServicioAutenticacion servicioAutenticacion,
                                    IRepositorioUsuario repositorioUsuario) {
        this.servicioAutenticacion = servicioAutenticacion;
        this.repositorioUsuario = repositorioUsuario;
    }

    /**
     * Ejecuta el registro completo de un nuevo usuario.
     *
     * @param correo     correo ingresado en el formulario
     * @param contrasena contraseña ingresada en el formulario
     */
    public void ejecutar(String correo, String contrasena)
            throws CorreoInvalidoException, ContrasenaInvalidaException,
                   CorreoYaRegistradoException, RegistroFallidoException {

        // 1) La validacion ocurre aca mismo, dentro del constructor de
        //    Usuario, que a su vez delega en los setters (setCorreo,
        //    setContrasena). Si algo esta mal, la excepcion se lanza
        //    antes de intentar contactar a Supabase.
        Usuario usuario = new Usuario(correo, contrasena);

        // 2) Verificacion adicional de correo repetido, antes de
        //    gastar una llamada de registro contra la autenticacion.
        if (repositorioUsuario.existeCorreo(usuario.getCorreo())) {
            throw new CorreoYaRegistradoException("Este correo ya está registrado.");
        }

        // 3) Registro en el proveedor de autenticacion (Supabase Auth).
        String idGenerado = servicioAutenticacion.registrar(usuario);

        // 4) Persistencia del perfil en la tabla "usuarios".
        repositorioUsuario.guardar(idGenerado, usuario);
    }
}
