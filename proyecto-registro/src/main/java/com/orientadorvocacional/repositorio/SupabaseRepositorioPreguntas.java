package com.orientadorvocacional.repositorio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orientadorvocacional.config.ConfiguracionSupabase;
import com.orientadorvocacional.excepciones.CuestionarioFallidoException;
import com.orientadorvocacional.modelo.OpcionRespuesta;
import com.orientadorvocacional.modelo.Pregunta;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Trae las preguntas y sus opciones desde Supabase, usando el
 * "embedding" (join) que ofrece PostgREST para traer todo en
 * una sola petición.
 */
public class SupabaseRepositorioPreguntas implements IRepositorioPreguntas {

    private final ConfiguracionSupabase configuracion;
    private final HttpClient clienteHttp;
    private final ObjectMapper mapeadorJson;

    public SupabaseRepositorioPreguntas(ConfiguracionSupabase configuracion) {
        this.configuracion = configuracion;
        this.clienteHttp = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapeadorJson = new ObjectMapper();
    }

    @Override
    public List<Pregunta> obtenerCuestionarioCompleto() throws CuestionarioFallidoException {
        try {
            // El "select" pide las preguntas y, anidadas, sus opciones
            // relacionadas (PostgREST arma el join automáticamente
            // gracias a la llave foránea pregunta_id).
            HttpRequest peticion = HttpRequest.newBuilder()
                        .uri(URI.create(configuracion.getUrlProyecto()
                            + "/rest/v1/preguntas?select=id,texto,categoria,"
                            + "opciones_respuesta(id,texto,area,peso)&order=id.asc"))
                    .header("apikey", configuracion.getClaveAnonPublica())
                    .GET()
                    .build();

            HttpResponse<String> respuesta =
                    clienteHttp.send(peticion, HttpResponse.BodyHandlers.ofString());

            if (respuesta.statusCode() != 200) {
                throw new CuestionarioFallidoException(
                        "No se pudo obtener el cuestionario (código " + respuesta.statusCode() + ").");
            }

            return convertirRespuesta(respuesta.body());

        } catch (IOException | InterruptedException excepcion) {
            throw new CuestionarioFallidoException(
                    "No se pudo obtener el cuestionario por un problema de conexión.", excepcion);
        }
    }

    private List<Pregunta> convertirRespuesta(String cuerpoJson) throws CuestionarioFallidoException {
        try {
            JsonNode arregloPreguntas = mapeadorJson.readTree(cuerpoJson);
            List<Pregunta> preguntas = new ArrayList<>();

            for (JsonNode nodoPregunta : arregloPreguntas) {
                List<OpcionRespuesta> opciones = new ArrayList<>();
                for (JsonNode nodoOpcion : nodoPregunta.get("opciones_respuesta")) {
                    opciones.add(new OpcionRespuesta(
                            nodoOpcion.get("id").asInt(),
                            nodoOpcion.get("texto").asText(),
                            nodoOpcion.get("area").asText(),
                            nodoOpcion.get("peso").asInt()
                    ));
                }

                preguntas.add(new Pregunta(
                        nodoPregunta.get("id").asInt(),
                        nodoPregunta.get("texto").asText(),
                        nodoPregunta.get("categoria").asText(),
                        opciones
                ));
            }

            return preguntas;

        } catch (IOException excepcion) {
            throw new CuestionarioFallidoException(
                    "La respuesta del cuestionario no se pudo interpretar.", excepcion);
        }
    }
}