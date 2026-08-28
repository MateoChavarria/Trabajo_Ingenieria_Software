package com.orientadorvocacional.repositorio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orientadorvocacional.config.ConfiguracionSupabase;
import com.orientadorvocacional.excepciones.RegistroFallidoException;
import com.orientadorvocacional.modelo.Usuario;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Implementacion de IRepositorioUsuario que usa la API REST
 * autogenerada de Supabase (PostgREST) para insertar y consultar
 * filas directamente en la tabla "usuarios", sin necesitar una
 * conexion JDBC aparte.
 */
public class SupabaseRepositorioUsuario implements IRepositorioUsuario {

    private final ConfiguracionSupabase configuracion;
    private final HttpClient clienteHttp;
    private final ObjectMapper mapeadorJson;

    public SupabaseRepositorioUsuario(ConfiguracionSupabase configuracion) {
        this.configuracion = configuracion;
        this.clienteHttp = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapeadorJson = new ObjectMapper();
    }

    @Override
    public void guardar(String id, Usuario usuario) throws RegistroFallidoException {
        try {
            String cuerpoJson = mapeadorJson.createObjectNode()
                    .put("id", id)
                    .put("correo", usuario.getCorreo())
                    .put("estado_cuenta", usuario.getEstadoCuenta().aTextoBaseDeDatos())
                    .toString();

            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(URI.create(configuracion.getUrlProyecto() + "/rest/v1/usuarios"))
                    .header("apikey", configuracion.getClaveAnonPublica())
                    .header("Authorization", "Bearer " + configuracion.getClaveAnonPublica())
                    .header("Content-Type", "application/json")
                    .header("Prefer", "return=minimal")
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpoJson))
                    .build();

            HttpResponse<String> respuesta =
                    clienteHttp.send(peticion, HttpResponse.BodyHandlers.ofString());

            if (!fueExitosa(respuesta.statusCode())) {
                throw new RegistroFallidoException(
                        "No se pudo guardar el perfil del usuario (código "
                                + respuesta.statusCode() + "): " + respuesta.body());
            }

        } catch (IOException | InterruptedException excepcion) {
            throw new RegistroFallidoException(
                    "No se pudo guardar el perfil por un problema de conexión.", excepcion);
        }
    }

    @Override
    public boolean existeCorreo(String correo) throws RegistroFallidoException {
        try {
            String correoCodificado = java.net.URLEncoder.encode(correo, StandardCharsets.UTF_8);

            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(URI.create(configuracion.getUrlProyecto()
                            + "/rest/v1/usuarios?correo=eq." + correoCodificado + "&select=id"))
                    .header("apikey", configuracion.getClaveAnonPublica())
                    .header("Authorization", "Bearer " + configuracion.getClaveAnonPublica())
                    .GET()
                    .build();

            HttpResponse<String> respuesta =
                    clienteHttp.send(peticion, HttpResponse.BodyHandlers.ofString());

            if (!fueExitosa(respuesta.statusCode())) {
                throw new RegistroFallidoException(
                        "No se pudo verificar el correo (código " + respuesta.statusCode() + ").");
            }

            JsonNodeCantidad resultado = new JsonNodeCantidad(mapeadorJson, respuesta.body());
            return resultado.tieneElementos();

        } catch (IOException | InterruptedException excepcion) {
            throw new RegistroFallidoException(
                    "No se pudo verificar el correo por un problema de conexión.", excepcion);
        }
    }

    private boolean fueExitosa(int codigoEstado) {
        return codigoEstado >= 200 && codigoEstado < 300;
    }

    /**
     * Pequeña clase de apoyo, solo para interpretar si el arreglo JSON
     * de respuesta viene vacío o con al menos un elemento.
     */
    private static class JsonNodeCantidad {
        private final boolean tieneElementos;

        JsonNodeCantidad(ObjectMapper mapeadorJson, String cuerpoJson) throws IOException {
            var nodo = mapeadorJson.readTree(cuerpoJson);
            this.tieneElementos = nodo.isArray() && nodo.size() > 0;
        }

        boolean tieneElementos() {
            return tieneElementos;
        }
    }
}
