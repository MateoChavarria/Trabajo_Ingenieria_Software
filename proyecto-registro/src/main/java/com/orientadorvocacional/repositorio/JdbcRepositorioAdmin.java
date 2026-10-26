package com.orientadorvocacional.repositorio;

import com.orientadorvocacional.excepciones.AdminException;
import com.orientadorvocacional.modelo.DatosAdmin;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Implementacion del panel de administracion con SQL directo (JdbcTemplate).
 * Todas las consultas usan parametros (?), nunca se arma SQL con texto del usuario.
 * Despues de cada cambio se vacian las memorias temporales para que la
 * aplicacion muestre los datos nuevos de inmediato.
 */
@Repository
public class JdbcRepositorioAdmin implements IRepositorioAdmin {

    private static final int LIMITE_FILAS = 500;

    private final JdbcTemplate jdbc;

    public JdbcRepositorioAdmin(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<String> obtenerCorreo(String usuarioId) throws AdminException {
        UUID id;
        try {
            id = UUID.fromString(usuarioId);
        } catch (IllegalArgumentException excepcion) {
            return Optional.empty();
        }
        return ejecutar("No se pudo verificar tu permiso.", () -> {
            List<String> correos = jdbc.queryForList("select correo from usuarios where id = ?", String.class, id);
            return correos.isEmpty() ? Optional.<String>empty() : Optional.ofNullable(correos.get(0));
        });
    }

    @Override
    public DatosAdmin.Resumen resumen() throws AdminException {
        return ejecutar("No se pudo consultar el resumen.", () -> new DatosAdmin.Resumen(
                contar("select count(*) from usuarios"),
                contar("select count(*) from usuarios where acceso_completo"),
                contar("select count(*) from pagos where estado = 'exitoso'"),
                contar("select coalesce(sum(monto), 0) from pagos where estado = 'exitoso'"),
                contar("select count(*) from resultados_test"),
                contar("select count(*) from carreras"),
                contar("select count(*) from universidades"),
                contar("select count(*) from preguntas")));
    }

    @Override
    public List<DatosAdmin.Usuario> listarUsuarios() throws AdminException {
        return ejecutar("No se pudo consultar los usuarios.", () -> jdbc.query(
                "select id::text as id, correo, fecha_registro, estado_cuenta, acceso_completo "
                        + "from usuarios order by fecha_registro desc limit " + LIMITE_FILAS,
                (rs, n) -> new DatosAdmin.Usuario(
                        rs.getString("id"), rs.getString("correo"),
                        texto(rs.getTimestamp("fecha_registro")),
                        rs.getString("estado_cuenta"), rs.getBoolean("acceso_completo"))));
    }

    @Override
    public List<DatosAdmin.Pago> listarPagos() throws AdminException {
        return ejecutar("No se pudo consultar los pagos.", () -> jdbc.query(
                "select p.id, u.correo, p.monto, p.estado, p.referencia, p.fecha, p.motivo_fallo "
                        + "from pagos p join usuarios u on u.id = p.usuario_id "
                        + "order by p.fecha desc limit " + LIMITE_FILAS,
                (rs, n) -> new DatosAdmin.Pago(
                        rs.getLong("id"), rs.getString("correo"), rs.getInt("monto"),
                        rs.getString("estado"), rs.getString("referencia"),
                        texto(rs.getTimestamp("fecha")), rs.getString("motivo_fallo"))));
    }

    // ------------------------------------------------------------------
    // Carreras
    // ------------------------------------------------------------------

    @Override
    public List<DatosAdmin.Carrera> listarCarreras() throws AdminException {
        return ejecutar("No se pudo consultar las carreras.", () -> jdbc.query(
                "select id, nombre, descripcion, campo_laboral, area_categoria, duracion_semestres, "
                        + "tarifa_semestre, universidad_id from carreras order by nombre",
                (rs, n) -> new DatosAdmin.Carrera(
                        rs.getInt("id"), rs.getString("nombre"), rs.getString("descripcion"),
                        rs.getString("campo_laboral"), rs.getString("area_categoria"),
                        rs.getObject("duracion_semestres", Integer.class),
                        rs.getBigDecimal("tarifa_semestre"),
                        rs.getObject("universidad_id", Integer.class))));
    }

    @Override
    public void crearCarrera(DatosAdmin.Carrera c) throws AdminException {
        ejecutar("No se pudo crear la carrera.", () -> {
            jdbc.update("insert into carreras (nombre, descripcion, campo_laboral, area_categoria, "
                            + "duracion_semestres, tarifa_semestre, universidad_id) values (?, ?, ?, ?, ?, ?, ?)",
                    c.nombre(), c.descripcion(), c.campoLaboral(), c.areaCategoria(),
                    c.duracionSemestres(), c.tarifaSemestre(), c.universidadId());
            CacheTemporal.invalidarTodas();
            return null;
        });
    }

    @Override
    public boolean actualizarCarrera(int id, DatosAdmin.Carrera c) throws AdminException {
        return ejecutar("No se pudo actualizar la carrera.", () -> {
            int filas = jdbc.update("update carreras set nombre = ?, descripcion = ?, campo_laboral = ?, "
                            + "area_categoria = ?, duracion_semestres = ?, tarifa_semestre = ?, universidad_id = ? "
                            + "where id = ?",
                    c.nombre(), c.descripcion(), c.campoLaboral(), c.areaCategoria(),
                    c.duracionSemestres(), c.tarifaSemestre(), c.universidadId(), id);
            CacheTemporal.invalidarTodas();
            return filas > 0;
        });
    }

    @Override
    public int contarSemestresDeCarrera(int id) throws AdminException {
        return ejecutar("No se pudo verificar la carrera.", () -> {
            Integer total = jdbc.queryForObject("select count(*) from semestres_carrera where carrera_id = ?",
                    Integer.class, id);
            return total == null ? 0 : total;
        });
    }

    @Override
    public boolean eliminarCarrera(int id) throws AdminException {
        return ejecutar("No se pudo eliminar la carrera.", () -> {
            int filas = jdbc.update("delete from carreras where id = ?", id);
            CacheTemporal.invalidarTodas();
            return filas > 0;
        });
    }

    // ------------------------------------------------------------------
    // Universidades
    // ------------------------------------------------------------------

    @Override
    public List<DatosAdmin.Universidad> listarUniversidades() throws AdminException {
        return ejecutar("No se pudo consultar las universidades.", () -> jdbc.query(
                "select id, nombre, caracter, municipio, tiene_informacion_completa "
                        + "from universidades order by nombre",
                (rs, n) -> new DatosAdmin.Universidad(
                        rs.getInt("id"), rs.getString("nombre"), rs.getString("caracter"),
                        rs.getString("municipio"), rs.getBoolean("tiene_informacion_completa"))));
    }

    @Override
    public boolean existeUniversidad(int id) throws AdminException {
        return ejecutar("No se pudo verificar la universidad.", () -> {
            Integer total = jdbc.queryForObject("select count(*) from universidades where id = ?",
                    Integer.class, id);
            return total != null && total > 0;
        });
    }

    @Override
    public void crearUniversidad(DatosAdmin.Universidad u) throws AdminException {
        ejecutar("No se pudo crear la universidad.", () -> {
            jdbc.update("insert into universidades (nombre, caracter, municipio, tiene_informacion_completa) "
                            + "values (?, ?, ?, ?)",
                    u.nombre(), u.caracter(), u.municipio(), u.tieneInformacionCompleta());
            CacheTemporal.invalidarTodas();
            return null;
        });
    }

    @Override
    public boolean actualizarUniversidad(int id, DatosAdmin.Universidad u) throws AdminException {
        return ejecutar("No se pudo actualizar la universidad.", () -> {
            int filas = jdbc.update("update universidades set nombre = ?, caracter = ?, municipio = ?, "
                            + "tiene_informacion_completa = ? where id = ?",
                    u.nombre(), u.caracter(), u.municipio(), u.tieneInformacionCompleta(), id);
            CacheTemporal.invalidarTodas();
            return filas > 0;
        });
    }

    @Override
    public int contarCarrerasDeUniversidad(int id) throws AdminException {
        return ejecutar("No se pudo verificar la universidad.", () -> {
            Integer total = jdbc.queryForObject("select count(*) from carreras where universidad_id = ?",
                    Integer.class, id);
            return total == null ? 0 : total;
        });
    }

    @Override
    public boolean eliminarUniversidad(int id) throws AdminException {
        return ejecutar("No se pudo eliminar la universidad.", () -> {
            int filas = jdbc.update("delete from universidades where id = ?", id);
            CacheTemporal.invalidarTodas();
            return filas > 0;
        });
    }

    // ------------------------------------------------------------------
    // Preguntas
    // ------------------------------------------------------------------

    @Override
    public List<DatosAdmin.Pregunta> listarPreguntas() throws AdminException {
        return ejecutar("No se pudo consultar las preguntas.", () -> {
            Map<Integer, List<DatosAdmin.Opcion>> opcionesPorPregunta = new LinkedHashMap<>();
            jdbc.query("select id, pregunta_id, texto, area, peso from opciones_respuesta order by pregunta_id, id",
                    rs -> {
                        opcionesPorPregunta
                                .computeIfAbsent(rs.getInt("pregunta_id"), clave -> new ArrayList<>())
                                .add(new DatosAdmin.Opcion(rs.getInt("id"), rs.getString("texto"),
                                        rs.getString("area"), rs.getInt("peso")));
                    });
            return jdbc.query("select id, texto, categoria from preguntas order by id",
                    (rs, n) -> new DatosAdmin.Pregunta(rs.getInt("id"), rs.getString("texto"),
                            rs.getString("categoria"),
                            opcionesPorPregunta.getOrDefault(rs.getInt("id"), List.of())));
        });
    }

    @Override
    public boolean actualizarTextoPregunta(int id, String texto) throws AdminException {
        return ejecutar("No se pudo actualizar la pregunta.", () -> {
            int filas = jdbc.update("update preguntas set texto = ? where id = ?", texto, id);
            CacheTemporal.invalidarTodas();
            return filas > 0;
        });
    }

    @Override
    public boolean actualizarTextoOpcion(int id, String texto) throws AdminException {
        return ejecutar("No se pudo actualizar la opción.", () -> {
            int filas = jdbc.update("update opciones_respuesta set texto = ? where id = ?", texto, id);
            CacheTemporal.invalidarTodas();
            return filas > 0;
        });
    }

    // ------------------------------------------------------------------

    private long contar(String sql) {
        Long valor = jdbc.queryForObject(sql, Long.class);
        return valor == null ? 0 : valor;
    }

    private static String texto(Timestamp marca) {
        return marca == null ? "" : marca.toLocalDateTime().toString();
    }

    /** Ejecuta la consulta y convierte cualquier falla de base de datos en un error 500 con mensaje claro. */
    private static <T> T ejecutar(String mensajeError, Supplier<T> accion) throws AdminException {
        try {
            return accion.get();
        } catch (RuntimeException excepcion) {
            throw AdminException.fallaInterna(mensajeError, excepcion);
        }
    }
}
