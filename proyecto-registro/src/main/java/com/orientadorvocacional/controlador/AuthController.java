package com.orientadorvocacional.controlador;

import com.orientadorvocacional.excepciones.*;
import com.orientadorvocacional.modelo.SesionUsuario;
import com.orientadorvocacional.servicio.CasoUsoIniciarSesion;
import com.orientadorvocacional.servicio.CasoUsoRegistrarUsuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Expone por HTTP los dos casos de uso de autenticacion, para que el
 * frontend (HTML/JavaScript) pueda llamarlos con fetch().
 *
 * @RestController = @Controller + @ResponseBody: cada metodo devuelve
 * directamente el cuerpo de la respuesta (texto o JSON), no el nombre
 * de una plantilla HTML que renderizar en el servidor.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CasoUsoRegistrarUsuario casoUsoRegistro;
    private final CasoUsoIniciarSesion casoUsoLogin;

    public AuthController(CasoUsoRegistrarUsuario casoUsoRegistro, CasoUsoIniciarSesion casoUsoLogin) {
        this.casoUsoRegistro = casoUsoRegistro;
        this.casoUsoLogin = casoUsoLogin;
    }

    /**
     * "record" de Java: una forma corta de declarar una clase inmutable
     * que solo carga datos (aqui, lo que el frontend manda en el
     * cuerpo de la peticion JSON: { "correo": "...", "contrasena": "..." }).
     */
    public record CredencialesRequest(String correo, String contrasena) {
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody CredencialesRequest datos) {
        try {
            casoUsoRegistro.ejecutar(datos.correo(), datos.contrasena());
            return ResponseEntity.ok().build();

        } catch (CorreoInvalidoException | ContrasenaInvalidaException excepcion) {
            return ResponseEntity.badRequest().body(excepcion.getMessage());
        } catch (CorreoYaRegistradoException excepcion) {
            return ResponseEntity.status(409).body(excepcion.getMessage());
        } catch (RegistroFallidoException excepcion) {
            return ResponseEntity.internalServerError().body(excepcion.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> iniciarSesion(@RequestBody CredencialesRequest datos) {
        try {
            SesionUsuario sesion = casoUsoLogin.ejecutar(datos.correo(), datos.contrasena());
            return ResponseEntity.ok(sesion);

        } catch (CredencialesInvalidasException excepcion) {
            return ResponseEntity.status(401).body(excepcion.getMessage());
        } catch (InicioSesionFallidoException excepcion) {
            return ResponseEntity.internalServerError().body(excepcion.getMessage());
        }
    }
}
