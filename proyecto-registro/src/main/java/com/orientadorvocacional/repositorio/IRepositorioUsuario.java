package com.orientadorvocacional.repositorio;

import com.orientadorvocacional.excepciones.RegistroFallidoException;
import com.orientadorvocacional.modelo.Usuario;

/**
 * Contrato para guardar y consultar el perfil del usuario en la
 * tabla "usuarios".
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
}