package com.orientadorvocacional.controlador;

import com.orientadorvocacional.excepciones.AdminException;
import com.orientadorvocacional.modelo.DatosAdmin;
import com.orientadorvocacional.seguridad.InterceptorAutenticacion;
import com.orientadorvocacional.servicio.ServicioAdmin;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Panel de administracion. Todas las rutas /api/admin/** pasan antes por
 * InterceptorAutenticacion e InterceptorAdmin. /api/yo/admin solo exige sesion
 * y sirve para que el menu sepa si debe mostrar el boton del panel. Los errores
 * (AdminException) los traduce ManejadorGlobalErrores.
 */
@RestController
public class AdminController {

    private final ServicioAdmin servicio;

    public AdminController(ServicioAdmin servicio) {
        this.servicio = servicio;
    }

    /** Solo dice si la cuenta con sesion es la administradora (no revela nada mas). */
    @GetMapping("/api/yo/admin")
    public ResponseEntity<?> soyAdmin(HttpServletRequest peticion) throws AdminException {
        String id = (String) peticion.getAttribute(InterceptorAutenticacion.ATRIBUTO_USUARIO_ID);
        return ResponseEntity.ok(Map.of("esAdmin", servicio.esAdministrador(id)));
    }

    @GetMapping("/api/admin/resumen")
    public ResponseEntity<?> resumen() throws AdminException {
        return ResponseEntity.ok(servicio.resumen());
    }

    @GetMapping("/api/admin/usuarios")
    public ResponseEntity<?> usuarios() throws AdminException {
        return ResponseEntity.ok(servicio.listarUsuarios());
    }

    @GetMapping("/api/admin/pagos")
    public ResponseEntity<?> pagos() throws AdminException {
        return ResponseEntity.ok(servicio.listarPagos());
    }

    // ---- Carreras ----

    @GetMapping("/api/admin/carreras")
    public ResponseEntity<?> carreras() throws AdminException {
        return ResponseEntity.ok(servicio.listarCarreras());
    }

    @PostMapping("/api/admin/carreras")
    public ResponseEntity<?> crearCarrera(@RequestBody DatosAdmin.Carrera datos) throws AdminException {
        servicio.crearCarrera(datos);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/api/admin/carreras/{id}")
    public ResponseEntity<?> actualizarCarrera(@PathVariable int id, @RequestBody DatosAdmin.Carrera datos) throws AdminException {
        servicio.actualizarCarrera(id, datos);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/admin/carreras/{id}")
    public ResponseEntity<?> eliminarCarrera(@PathVariable int id) throws AdminException {
        servicio.eliminarCarrera(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Universidades ----

    @GetMapping("/api/admin/universidades")
    public ResponseEntity<?> universidades() throws AdminException {
        return ResponseEntity.ok(servicio.listarUniversidades());
    }

    @PostMapping("/api/admin/universidades")
    public ResponseEntity<?> crearUniversidad(@RequestBody DatosAdmin.Universidad datos) throws AdminException {
        servicio.crearUniversidad(datos);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/api/admin/universidades/{id}")
    public ResponseEntity<?> actualizarUniversidad(@PathVariable int id, @RequestBody DatosAdmin.Universidad datos) throws AdminException {
        servicio.actualizarUniversidad(id, datos);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/admin/universidades/{id}")
    public ResponseEntity<?> eliminarUniversidad(@PathVariable int id) throws AdminException {
        servicio.eliminarUniversidad(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Preguntas del test ----

    @GetMapping("/api/admin/preguntas")
    public ResponseEntity<?> preguntas() throws AdminException {
        return ResponseEntity.ok(servicio.listarPreguntas());
    }

    @PutMapping("/api/admin/preguntas/{id}")
    public ResponseEntity<?> actualizarPregunta(@PathVariable int id, @RequestBody DatosAdmin.TextoNuevo datos) throws AdminException {
        servicio.actualizarTextoPregunta(id, datos);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/admin/opciones/{id}")
    public ResponseEntity<?> actualizarOpcion(@PathVariable int id, @RequestBody DatosAdmin.TextoNuevo datos) throws AdminException {
        servicio.actualizarTextoOpcion(id, datos);
        return ResponseEntity.noContent().build();
    }
}
