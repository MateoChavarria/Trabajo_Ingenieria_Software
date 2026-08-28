package com.orientadorvocacional.repositorio;

import com.orientadorvocacional.excepciones.RegistroFallidoException;
import com.orientadorvocacional.modelo.Usuario;

/**
 * Contrato para guardar y consultar el perfil del usuario en la
 * tabla "usuarios". Esta responsabilidad es independiente de la
 * autenticacion (por eso es una interfaz distinta a
 * IServicioAutenticacion, siguiendo el principio de segregacion
 * de interfaces: cada una tiene un proposito claro y pequeño).
 */
public interface IRepositorioUsuario {

    /**
     * Guarda el perfil del usuario, usando el id que ya le asigno
     * el proveedor de autenticacion.
     *
     * @param id      identificador devuelto por el servicio de autenticacion
     * @param usuario datos ya validados del usuario
     */
    void guardar(String id, Usuario usuario) throws RegistroFallidoException;

    /**
     * Verifica si ya existe un perfil guardado con ese correo.
     * Sirve como segunda capa de verificacion, ademas de la que ya
     * hace Supabase Auth y la restriccion "unique" de la tabla.
     */
    boolean existeCorreo(String correo) throws RegistroFallidoException;
}
