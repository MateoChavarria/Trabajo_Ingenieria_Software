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
import java.time.Duration;

/**
 * Implementacion de IRepositorioUsuario que usa la API REST
 * autogenerada de Supabase (PostgREST).
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

            // Nota: al registrarse, el usuario aun no tiene un token de
            // sesion propio (recien se esta creando su cuenta). Por eso
            // este INSERT sigue yendo solo con "apikey" — coincide con
            // la politica "Insertar perfil propio al registrarse", que
            // permite el insert tanto a "anon" como a "authenticated".
            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(URI.create(configuracion.getUrlProyecto() + "/rest/v1/usuarios"))
                    .header("apikey", configuracion.getClaveAnonPublica())
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

    /**
     * Consulta el perfil del usuario logueado, usando SU PROPIO token de
     * sesion. Este es el patron que usaremos de ahora en adelante para
     * cualquier peticion que necesite identificar "de quien" son los datos:
     * se manda el token en el header Authorization, y la politica de RLS
     * en Supabase (auth.uid() = id) se encarga de que solo pueda ver su
     * propia fila, nunca la de otro usuario.
     */
    public String consultarPerfilPropio(String tokenAcceso) throws RegistroFallidoException {
        try {
            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(URI.create(configuracion.getUrlProyecto() + "/rest/v1/usuarios?select=*"))
                    .header("apikey", configuracion.getClaveAnonPublica())
                    .header("Authorization", "Bearer " + tokenAcceso)
                    .GET()
                    .build();

            HttpResponse<String> respuesta =
                    clienteHttp.send(peticion, HttpResponse.BodyHandlers.ofString());

            if (!fueExitosa(respuesta.statusCode())) {
                throw new RegistroFallidoException(
                        "No se pudo consultar el perfil (código " + respuesta.statusCode() + ").");
            }

            return respuesta.body();

        } catch (IOException | InterruptedException excepcion) {
            throw new RegistroFallidoException(
                    "No se pudo consultar el perfil por un problema de conexión.", excepcion);
        }
    }

    private boolean fueExitosa(int codigoEstado) {
        return codigoEstado >= 200 && codigoEstado < 300;
    }
}