package com.orientadorvocacional.repositorio;

import com.orientadorvocacional.entidad.UsuarioEntity;
import com.orientadorvocacional.excepciones.RegistroFallidoException;
import com.orientadorvocacional.modelo.Usuario;
import com.orientadorvocacional.repositorio.jpa.UsuarioJpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Implementacion de IRepositorioUsuario que guarda el perfil
 * directamente en PostgreSQL usando JPA (JDBC por dentro), en vez de
 * llamar a la API REST de Supabase como hacia la version anterior
 * (SupabaseRepositorioUsuario, ya eliminada).
 *
 * Esta clase actua de "traductor" entre el mundo de negocio (Usuario)
 * y el mundo de persistencia (UsuarioEntity) — asi el resto del
 * sistema (CasoUsoRegistrarUsuario) sigue dependiendo solo de la
 * interfaz IRepositorioUsuario, sin enterarse de que por dentro ahora
 * se usa JPA en vez de HTTP.
 */
@Repository
public class JpaRepositorioUsuario implements IRepositorioUsuario {

    private final UsuarioJpaRepository repositorioJpa;

    public JpaRepositorioUsuario(UsuarioJpaRepository repositorioJpa) {
        this.repositorioJpa = repositorioJpa;
    }

    @Override
    public void guardar(String id, Usuario usuario) throws RegistroFallidoException {
        try {
                    UsuarioEntity entidad = new UsuarioEntity(
                    java.util.UUID.fromString(id),
                    usuario.getCorreo(),
                    usuario.getFechaRegistro(),
                    usuario.getEstadoCuenta().aTextoBaseDeDatos()
            );
            repositorioJpa.save(entidad);

        } catch (Exception excepcion) {
            // Cualquier problema de conexion o de restriccion en la base
            // de datos (por ejemplo, un correo duplicado) llega aca como
            // una excepcion generica de Spring; la envolvemos en nuestra
            // excepcion propia para no filtrar detalles internos de JPA
            // hacia las capas de arriba.
            throw new RegistroFallidoException(
                    "No se pudo guardar el perfil del usuario en la base de datos.", excepcion);
        }
    }
}
