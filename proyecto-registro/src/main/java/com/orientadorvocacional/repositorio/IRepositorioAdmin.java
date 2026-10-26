package com.orientadorvocacional.repositorio;

import com.orientadorvocacional.excepciones.AdminException;
import com.orientadorvocacional.modelo.DatosAdmin;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de todo lo que necesita el panel de administracion:
 * consultas de gestion y edicion del catalogo. Los datos ya vienen validados
 * desde ServicioAdmin. Los metodos "actualizar" y "eliminar" devuelven false
 * si el registro no existe.
 */
public interface IRepositorioAdmin {

    /** Correo registrado para el id del usuario (vacio si no hay perfil). */
    Optional<String> obtenerCorreo(String usuarioId) throws AdminException;

    DatosAdmin.Resumen resumen() throws AdminException;

    List<DatosAdmin.Usuario> listarUsuarios() throws AdminException;

    List<DatosAdmin.Pago> listarPagos() throws AdminException;

    // ---- Carreras ----
    List<DatosAdmin.Carrera> listarCarreras() throws AdminException;

    void crearCarrera(DatosAdmin.Carrera carrera) throws AdminException;

    boolean actualizarCarrera(int id, DatosAdmin.Carrera carrera) throws AdminException;

    int contarSemestresDeCarrera(int id) throws AdminException;

    boolean eliminarCarrera(int id) throws AdminException;

    // ---- Universidades ----
    List<DatosAdmin.Universidad> listarUniversidades() throws AdminException;

    boolean existeUniversidad(int id) throws AdminException;

    void crearUniversidad(DatosAdmin.Universidad universidad) throws AdminException;

    boolean actualizarUniversidad(int id, DatosAdmin.Universidad universidad) throws AdminException;

    int contarCarrerasDeUniversidad(int id) throws AdminException;

    boolean eliminarUniversidad(int id) throws AdminException;

    // ---- Preguntas del test (solo se edita el texto) ----
    List<DatosAdmin.Pregunta> listarPreguntas() throws AdminException;

    boolean actualizarTextoPregunta(int id, String texto) throws AdminException;

    boolean actualizarTextoOpcion(int id, String texto) throws AdminException;
}
