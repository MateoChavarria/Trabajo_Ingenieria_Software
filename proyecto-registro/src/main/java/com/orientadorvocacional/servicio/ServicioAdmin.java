package com.orientadorvocacional.servicio;

import com.orientadorvocacional.config.ConfiguracionAdmin;
import com.orientadorvocacional.excepciones.AdminException;
import com.orientadorvocacional.modelo.AreasVocacionales;
import com.orientadorvocacional.modelo.DatosAdmin;
import com.orientadorvocacional.repositorio.IRepositorioAdmin;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Casos de uso del panel de administracion: quien es administrador y las
 * operaciones de gestion, con sus validaciones. Los errores de datos salen como
 * IllegalArgumentException (el manejador global los convierte en 400).
 */
@Service
public class ServicioAdmin {

    private static final BigDecimal TARIFA_MAXIMA = new BigDecimal("100000000");

    private final IRepositorioAdmin repositorio;
    private final ConfiguracionAdmin configuracion;

    public ServicioAdmin(IRepositorioAdmin repositorio, ConfiguracionAdmin configuracion) {
        this.repositorio = repositorio;
        this.configuracion = configuracion;
    }

    /** true solo si el correo registrado del usuario es exactamente el configurado como administrador. */
    public boolean esAdministrador(String usuarioId) throws AdminException {
        Optional<String> correo = repositorio.obtenerCorreo(usuarioId);
        return correo.isPresent() && configuracion.esAdministrador(correo.get());
    }

    // ---- Consultas ----

    public DatosAdmin.Resumen resumen() throws AdminException {
        return repositorio.resumen();
    }

    public List<DatosAdmin.Usuario> listarUsuarios() throws AdminException {
        return repositorio.listarUsuarios();
    }

    public List<DatosAdmin.Pago> listarPagos() throws AdminException {
        return repositorio.listarPagos();
    }

    public List<DatosAdmin.Carrera> listarCarreras() throws AdminException {
        return repositorio.listarCarreras();
    }

    public List<DatosAdmin.Universidad> listarUniversidades() throws AdminException {
        return repositorio.listarUniversidades();
    }

    public List<DatosAdmin.Pregunta> listarPreguntas() throws AdminException {
        return repositorio.listarPreguntas();
    }

    // ---- Carreras ----

    public void crearCarrera(DatosAdmin.Carrera datos) throws AdminException {
        repositorio.crearCarrera(validarCarrera(datos));
    }

    public void actualizarCarrera(int id, DatosAdmin.Carrera datos) throws AdminException {
        if (!repositorio.actualizarCarrera(id, validarCarrera(datos))) {
            throw AdminException.noEncontrado("No existe la carrera indicada.");
        }
    }

    public void eliminarCarrera(int id) throws AdminException {
        if (repositorio.contarSemestresDeCarrera(id) > 0) {
            throw AdminException.conflicto(
                    "No se puede eliminar: la carrera ya tiene plan de estudios cargado.");
        }
        if (!repositorio.eliminarCarrera(id)) {
            throw AdminException.noEncontrado("No existe la carrera indicada.");
        }
    }

    private DatosAdmin.Carrera validarCarrera(DatosAdmin.Carrera datos) throws AdminException {
        if (datos == null) {
            throw new IllegalArgumentException("Faltan los datos de la carrera.");
        }
        String nombre = texto(datos.nombre(), "El nombre de la carrera", 3, 120);
        String descripcion = texto(datos.descripcion(), "La descripción", 10, 4000);
        String campo = texto(datos.campoLaboral(), "El campo laboral", 10, 4000);
        if (!AreasVocacionales.esValida(datos.areaCategoria())) {
            throw new IllegalArgumentException("El área de la carrera no es válida.");
        }
        Integer duracion = datos.duracionSemestres();
        if (duracion == null || duracion < 1 || duracion > 20) {
            throw new IllegalArgumentException("La duración debe estar entre 1 y 20 semestres.");
        }
        BigDecimal tarifa = datos.tarifaSemestre();
        if (tarifa != null && (tarifa.signum() < 0 || tarifa.compareTo(TARIFA_MAXIMA) > 0)) {
            throw new IllegalArgumentException("La tarifa por semestre no es válida.");
        }
        Integer universidadId = datos.universidadId();
        if (universidadId == null || !repositorio.existeUniversidad(universidadId)) {
            throw new IllegalArgumentException("La universidad indicada no existe.");
        }
        return new DatosAdmin.Carrera(datos.id(), nombre, descripcion, campo, datos.areaCategoria(),
                duracion, tarifa, universidadId);
    }

    // ---- Universidades ----

    public void crearUniversidad(DatosAdmin.Universidad datos) throws AdminException {
        repositorio.crearUniversidad(validarUniversidad(datos));
    }

    public void actualizarUniversidad(int id, DatosAdmin.Universidad datos) throws AdminException {
        if (!repositorio.actualizarUniversidad(id, validarUniversidad(datos))) {
            throw AdminException.noEncontrado("No existe la universidad indicada.");
        }
    }

    public void eliminarUniversidad(int id) throws AdminException {
        if (repositorio.contarCarrerasDeUniversidad(id) > 0) {
            throw AdminException.conflicto("No se puede eliminar: la universidad tiene carreras asociadas.");
        }
        if (!repositorio.eliminarUniversidad(id)) {
            throw AdminException.noEncontrado("No existe la universidad indicada.");
        }
    }

    private DatosAdmin.Universidad validarUniversidad(DatosAdmin.Universidad datos) {
        if (datos == null) {
            throw new IllegalArgumentException("Faltan los datos de la universidad.");
        }
        String nombre = texto(datos.nombre(), "El nombre de la universidad", 3, 150);
        String municipio = texto(datos.municipio(), "El municipio", 2, 150);
        String caracter = datos.caracter() == null ? "" : datos.caracter().trim();
        if (!caracter.equals("Pública") && !caracter.equals("Privada")) {
            throw new IllegalArgumentException("El carácter debe ser Pública o Privada.");
        }
        return new DatosAdmin.Universidad(datos.id(), nombre, caracter, municipio,
                datos.tieneInformacionCompleta());
    }

    // ---- Preguntas ----

    public void actualizarTextoPregunta(int id, DatosAdmin.TextoNuevo datos) throws AdminException {
        String texto = texto(datos == null ? null : datos.texto(), "El texto de la pregunta", 5, 300);
        if (!repositorio.actualizarTextoPregunta(id, texto)) {
            throw AdminException.noEncontrado("No existe la pregunta indicada.");
        }
    }

    public void actualizarTextoOpcion(int id, DatosAdmin.TextoNuevo datos) throws AdminException {
        String texto = texto(datos == null ? null : datos.texto(), "El texto de la opción", 2, 300);
        if (!repositorio.actualizarTextoOpcion(id, texto)) {
            throw AdminException.noEncontrado("No existe la opción indicada.");
        }
    }

    private static String texto(String valor, String campo, int minimo, int maximo) {
        String limpio = valor == null ? "" : valor.trim();
        if (limpio.length() < minimo || limpio.length() > maximo) {
            throw new IllegalArgumentException(campo + " debe tener entre " + minimo + " y " + maximo + " caracteres.");
        }
        return limpio;
    }
}
