package com.orientadorvocacional.servicio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orientadorvocacional.config.ConfiguracionSupabase;
import com.orientadorvocacional.excepciones.CorreoYaRegistradoException;
import com.orientadorvocacional.excepciones.RegistroFallidoException;
import com.orientadorvocacional.modelo.Usuario;
import com.orientadorvocacional.excepciones.CredencialesInvalidasException;
import com.orientadorvocacional.excepciones.InicioSesionFallidoException;
import com.orientadorvocacional.modelo.SesionUsuario;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementacion de IServicioAutenticacion que se conecta a la API de
 * autenticacion de Supabase (Supabase Auth).
 *
 * Responsabilidad unica de esta clase: registrar, iniciar sesion y
 * validar tokens de un usuario contra el sistema de autenticacion. NO
 * se encarga de guardar el perfil en la tabla "usuarios" (eso lo hace
 * JpaRepositorioUsuario, que persiste directo en PostgreSQL via JDBC/JPA).
 *
 * @Service le dice a Spring que cree UNA sola instancia de esta clase
 * (un "bean") y la entregue automaticamente a quien la necesite en su
 * constructor (por ejemplo, CasoUsoRegistrarUsuario), sin que nadie
 * tenga que escribir "new SupabaseServicioAutenticacion(...)" a mano.
 */
@Service
public class SupabaseServicioAutenticacion implements IServicioAutenticacion {

    private static final Logger LOG = LoggerFactory.getLogger(SupabaseServicioAutenticacion.class);

    private static final int HTTP_OK = 200;
    private static final int HTTP_CREADO = 201;
    private static final int HTTP_NO_AUTORIZADO = 401;
    private static final int HTTP_PROHIBIDO = 403;
    private static final int HTTP_DEMASIADAS_PETICIONES = 429;
    private static final int HTTP_ERROR_DEL_SERVIDOR = 500;

    private static final Duration TIEMPO_MAXIMO_PETICION = Duration.ofSeconds(15);

    /** Duracion por defecto de un access_token de Supabase si no informa "expires_in". */
    private static final long SEGUNDOS_VALIDEZ_POR_DEFECTO = 3600;

    /**
     * Cache corta de tokens ya validados, para no consultar a Supabase
     * en cada peticion. Se guarda el hash SHA-256 del token, nunca el
     * token en texto plano.
     */
    private static final long DURACION_CACHE_TOKEN_MS = 60_000;
    private static final int TAMANO_MAXIMO_CACHE = 10_000;

    private record TokenValidado(String idUsuario, long expiraEnMs) {
    }

    private final ConfiguracionSupabase configuracion;
    private final HttpClient clienteHttp;
    private final ObjectMapper mapeadorJson;
    private final Map<String, TokenValidado> cacheTokens = new ConcurrentHashMap<>();

    public SupabaseServicioAutenticacion(ConfiguracionSupabase configuracion) {
        this.configuracion = configuracion;
        this.clienteHttp = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapeadorJson = new ObjectMapper();
    }

    @Override
    public String registrar(Usuario usuario)
            throws CorreoYaRegistradoException, RegistroFallidoException {
        try {
            String cuerpoJson = mapeadorJson.createObjectNode()
                    .put("email", usuario.getCorreo())
                    .put("password", usuario.getContrasena())
                    .toString();

            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(URI.create(configuracion.getUrlProyecto() + "/auth/v1/signup"))
                    .timeout(TIEMPO_MAXIMO_PETICION)
                    .header("apikey", configuracion.getClaveAnonPublica())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpoJson))
                    .build();

            HttpResponse<String> respuesta =
                    clienteHttp.send(peticion, HttpResponse.BodyHandlers.ofString());

            return procesarRespuesta(respuesta);

        } catch (InterruptedException excepcion) {
            Thread.currentThread().interrupt();
            throw new RegistroFallidoException(
                    "No se pudo completar el registro por un problema de conexión.", excepcion);
        } catch (IOException excepcion) {
            throw new RegistroFallidoException(
                    "No se pudo completar el registro por un problema de conexión.", excepcion);
        }
    }

    @Override
    public SesionUsuario iniciarSesion(String correo, String contrasena)
            throws CredencialesInvalidasException, InicioSesionFallidoException {
        try {
            String cuerpoJson = mapeadorJson.createObjectNode()
                    .put("email", correo)
                    .put("password", contrasena)
                    .toString();

            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(URI.create(configuracion.getUrlProyecto()
                            + "/auth/v1/token?grant_type=password"))
                    .timeout(TIEMPO_MAXIMO_PETICION)
                    .header("apikey", configuracion.getClaveAnonPublica())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpoJson))
                    .build();

            HttpResponse<String> respuesta =
                    clienteHttp.send(peticion, HttpResponse.BodyHandlers.ofString());

            return procesarRespuestaLogin(respuesta);

        } catch (InterruptedException excepcion) {
            Thread.currentThread().interrupt();
            throw new InicioSesionFallidoException(
                    "No se pudo iniciar sesión por un problema de conexión.", excepcion);
        } catch (IOException excepcion) {
            throw new InicioSesionFallidoException(
                    "No se pudo iniciar sesión por un problema de conexión.", excepcion);
        }
    }

    @Override
    public String validarToken(String tokenAcceso)
            throws CredencialesInvalidasException, InicioSesionFallidoException {
        if (tokenAcceso == null || tokenAcceso.isBlank()) {
            throw new CredencialesInvalidasException("Sesión no válida.");
        }

        String huella = huellaSha256(tokenAcceso);
        long ahora = System.currentTimeMillis();
        TokenValidado enCache = cacheTokens.get(huella);
        if (enCache != null && enCache.expiraEnMs() > ahora) {
            return enCache.idUsuario();
        }

        try {
            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(URI.create(configuracion.getUrlProyecto() + "/auth/v1/user"))
                    .timeout(TIEMPO_MAXIMO_PETICION)
                    .header("apikey", configuracion.getClaveAnonPublica())
                    .header("Authorization", "Bearer " + tokenAcceso)
                    .GET()
                    .build();

            HttpResponse<String> respuesta =
                    clienteHttp.send(peticion, HttpResponse.BodyHandlers.ofString());

            int estado = respuesta.statusCode();
            if (estado == HTTP_NO_AUTORIZADO || estado == HTTP_PROHIBIDO) {
                throw new CredencialesInvalidasException("Sesión no válida o expirada.");
            }
            if (estado != HTTP_OK) {
                LOG.warn("Supabase respondio {} al validar un token", estado);
                throw new InicioSesionFallidoException("No se pudo verificar la sesión.");
            }

            String idUsuario = mapeadorJson.readTree(respuesta.body()).path("id").asText(null);
            if (idUsuario == null || idUsuario.isBlank()) {
                throw new CredencialesInvalidasException("Sesión no válida.");
            }

            guardarEnCache(huella, idUsuario, ahora);
            return idUsuario;

        } catch (JsonProcessingException excepcion) {
            throw new InicioSesionFallidoException("No se pudo verificar la sesión.", excepcion);
        } catch (InterruptedException excepcion) {
            Thread.currentThread().interrupt();
            throw new InicioSesionFallidoException("No se pudo verificar la sesión.", excepcion);
        } catch (IOException excepcion) {
            throw new InicioSesionFallidoException("No se pudo verificar la sesión.", excepcion);
        }
    }

    private void guardarEnCache(String huella, String idUsuario, long ahora) {
        if (cacheTokens.size() >= TAMANO_MAXIMO_CACHE) {
            cacheTokens.values().removeIf(entrada -> entrada.expiraEnMs() <= ahora);
            if (cacheTokens.size() >= TAMANO_MAXIMO_CACHE) {
                cacheTokens.clear();
            }
        }
        cacheTokens.put(huella, new TokenValidado(idUsuario, ahora + DURACION_CACHE_TOKEN_MS));
    }

    private static String huellaSha256(String texto) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(texto.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException excepcion) {
            throw new IllegalStateException("SHA-256 no disponible", excepcion);
        }
    }

    private SesionUsuario procesarRespuestaLogin(HttpResponse<String> respuesta)
            throws CredencialesInvalidasException, InicioSesionFallidoException {
        try {
            int estado = respuesta.statusCode();

            if (estado == HTTP_OK) {
                JsonNode raiz = mapeadorJson.readTree(respuesta.body());
                String token = raiz.path("access_token").asText(null);
                String idUsuario = raiz.path("user").path("id").asText(null);
                long segundosValidez = raiz.path("expires_in").asLong(SEGUNDOS_VALIDEZ_POR_DEFECTO);

                if (token == null || idUsuario == null) {
                    throw new InicioSesionFallidoException(
                            "Supabase no devolvió un token de sesión válido.");
                }
                return new SesionUsuario(token, idUsuario, segundosValidez);
            }

            if (estado == HTTP_DEMASIADAS_PETICIONES) {
                throw new InicioSesionFallidoException(
                        "Demasiados intentos. Espera unos minutos e inténtalo de nuevo.");
            }

            if (estado >= HTTP_ERROR_DEL_SERVIDOR) {
                LOG.warn("Supabase respondio {} al iniciar sesion", estado);
                throw new InicioSesionFallidoException(
                        "No se pudo iniciar sesión. Intenta más tarde.");
            }

            // Mensaje genérico a propósito: nunca decimos si falló el
            // correo o la contraseña, para no darle pistas a un atacante.
            throw new CredencialesInvalidasException(
                    "Correo o contraseña incorrectos.");

        } catch (JsonProcessingException excepcion) {
            throw new InicioSesionFallidoException(
                    "La respuesta del servidor no se pudo interpretar.", excepcion);
        }
    }

    private String procesarRespuesta(HttpResponse<String> respuesta)
            throws CorreoYaRegistradoException, RegistroFallidoException {
        try {
            JsonNode raiz = mapeadorJson.readTree(respuesta.body());

            boolean exito = respuesta.statusCode() == HTTP_OK || respuesta.statusCode() == HTTP_CREADO;

            if (exito) {
                JsonNode nodoId = raiz.has("id") ? raiz.get("id")
                        : raiz.path("user").get("id");

                if (nodoId == null) {
                    throw new RegistroFallidoException(
                            "Supabase no devolvió un id de usuario válido.");
                }
                return nodoId.asText();
            }

            String mensajeError = extraerMensajeDeError(raiz);

            if (esErrorDeCorreoRepetido(mensajeError)) {
                throw new CorreoYaRegistradoException("Este correo ya está registrado.");
            }

            if (respuesta.statusCode() == 429) {
                throw new RegistroFallidoException(
                        "Demasiados intentos. Espera unos minutos e inténtalo de nuevo.");
            }

            // El detalle tecnico de Supabase se registra en el log del
            // servidor, pero al usuario solo se le muestra un mensaje generico.
            LOG.warn("Supabase rechazo el registro ({}): {}", respuesta.statusCode(), mensajeError);
            throw new RegistroFallidoException(
                    "No se pudo completar el registro. Verifica tus datos e inténtalo de nuevo.");

        } catch (JsonProcessingException excepcion) {
            throw new RegistroFallidoException(
                    "La respuesta del servidor no se pudo interpretar.", excepcion);
        }
    }

    private String extraerMensajeDeError(JsonNode raiz) {
        if (raiz.has("msg")) {
            return raiz.get("msg").asText();
        }
        if (raiz.has("error_description")) {
            return raiz.get("error_description").asText();
        }
        if (raiz.has("error_code")) {
            return raiz.get("error_code").asText();
        }
        return "Error desconocido al registrar el usuario.";
    }

    private boolean esErrorDeCorreoRepetido(String mensajeError) {
        String mensajeEnMinuscula = mensajeError.toLowerCase();
        return mensajeEnMinuscula.contains("already registered")
                || mensajeEnMinuscula.contains("already exists")
                || mensajeEnMinuscula.contains("user_already_exists")
                || mensajeEnMinuscula.contains("email_exists");
    }
}