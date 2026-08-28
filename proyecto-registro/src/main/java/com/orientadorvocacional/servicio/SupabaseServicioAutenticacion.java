package com.orientadorvocacional.servicio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orientadorvocacional.config.ConfiguracionSupabase;
import com.orientadorvocacional.excepciones.CorreoYaRegistradoException;
import com.orientadorvocacional.excepciones.RegistroFallidoException;
import com.orientadorvocacional.modelo.Usuario;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Implementacion de IServicioAutenticacion que se conecta a la API de
 * autenticacion de Supabase (Supabase Auth).
 *
 * Responsabilidad unica de esta clase: registrar al usuario en el
 * sistema de autenticacion. NO se encarga de guardar el perfil en la
 * tabla "usuarios" (eso lo hace SupabaseRepositorioUsuario).
 */
public class SupabaseServicioAutenticacion implements IServicioAutenticacion {

    private final ConfiguracionSupabase configuracion;
    private final HttpClient clienteHttp;
    private final ObjectMapper mapeadorJson;

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
                    .header("apikey", configuracion.getClaveAnonPublica())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpoJson))
                    .build();

            HttpResponse<String> respuesta =
                    clienteHttp.send(peticion, HttpResponse.BodyHandlers.ofString());

            return procesarRespuesta(respuesta);

        } catch (IOException | InterruptedException excepcion) {
            throw new RegistroFallidoException(
                    "No se pudo completar el registro por un problema de conexión.", excepcion);
        }
    }

    private String procesarRespuesta(HttpResponse<String> respuesta)
            throws CorreoYaRegistradoException, RegistroFallidoException {
        try {
            JsonNode raiz = mapeadorJson.readTree(respuesta.body());

            boolean exito = respuesta.statusCode() == 200 || respuesta.statusCode() == 201;

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

            throw new RegistroFallidoException(mensajeError);

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
        return "Error desconocido al registrar el usuario.";
    }

    private boolean esErrorDeCorreoRepetido(String mensajeError) {
        String mensajeEnMinuscula = mensajeError.toLowerCase();
        return mensajeEnMinuscula.contains("already registered")
                || mensajeEnMinuscula.contains("already exists")
                || mensajeEnMinuscula.contains("user_already_exists");
    }
}
