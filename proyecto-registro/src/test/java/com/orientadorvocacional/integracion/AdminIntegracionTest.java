package com.orientadorvocacional.integracion;

import com.orientadorvocacional.config.ConfiguracionAdmin;
import com.orientadorvocacional.controlador.AdminController;
import com.orientadorvocacional.controlador.ManejadorGlobalErrores;
import com.orientadorvocacional.excepciones.AdminException;
import com.orientadorvocacional.modelo.DatosAdmin;
import com.orientadorvocacional.repositorio.IRepositorioAdmin;
import com.orientadorvocacional.seguridad.InterceptorAdmin;
import com.orientadorvocacional.seguridad.InterceptorAutenticacion;
import com.orientadorvocacional.servicio.ServicioAdmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integracion del panel de administracion: solo la cuenta administradora
 * (USUARIO_A, correo admin@test.com) puede entrar; USUARIO_B es un usuario normal.
 * Usa los interceptores y el servicio reales con un repositorio en memoria.
 */
class AdminIntegracionTest extends IntegracionBase {

    private static final String CORREO_ADMIN = "admin@test.com";
    private static final String CORREO_NORMAL = "normal@test.com";

    private RepositorioAdminFalso repositorioAdmin;
    private MockMvc mvcAdmin;

    @BeforeEach
    void montarPanel() {
        repositorioAdmin = new RepositorioAdminFalso();
        repositorioAdmin.correosPorId.put(USUARIO_A, CORREO_ADMIN);
        repositorioAdmin.correosPorId.put(USUARIO_B, CORREO_NORMAL);
        mvcAdmin = construirMvc(CORREO_ADMIN);
    }

    private MockMvc construirMvc(String correoAdministrador) {
        var servicio = new ServicioAdmin(repositorioAdmin, new ConfiguracionAdmin(correoAdministrador));
        return MockMvcBuilders
                .standaloneSetup(new AdminController(servicio))
                .setControllerAdvice(new ManejadorGlobalErrores())
                .setMessageConverters(new StringHttpMessageConverter(StandardCharsets.UTF_8),
                        new MappingJackson2HttpMessageConverter())
                .addMappedInterceptors(new String[]{"/api/yo/admin", "/api/admin/**"},
                        new InterceptorAutenticacion(autenticacion))
                .addMappedInterceptors(new String[]{"/api/admin/**"}, new InterceptorAdmin(servicio))
                .build();
    }

    private MockHttpServletRequestBuilder conJson(MockHttpServletRequestBuilder peticion, String json) {
        return peticion.contentType(MediaType.APPLICATION_JSON).content(json);
    }

    private static String jsonCarrera(String nombre, String area, int universidadId) {
        return "{\"nombre\":\"" + nombre + "\",\"descripcion\":\"Descripción de la carrera de prueba\","
                + "\"campoLaboral\":\"Campo laboral de la carrera de prueba\",\"areaCategoria\":\"" + area + "\","
                + "\"duracionSemestres\":9,\"tarifaSemestre\":1000000,\"universidadId\":" + universidadId + "}";
    }

    // ------------------------------------------------------------ acceso
    @Test
    @DisplayName("Sin sesion, el panel responde 401")
    void sinSesionNoSePuedeEntrar() throws Exception {
        mvcAdmin.perform(get("/api/admin/resumen")).andExpect(status().isUnauthorized());
        mvcAdmin.perform(get("/api/yo/admin")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Un usuario normal recibe 403 en TODAS las rutas del panel")
    void usuarioNormalNoPuedeEntrar() throws Exception {
        String[] rutas = {"/api/admin/resumen", "/api/admin/usuarios", "/api/admin/pagos",
                "/api/admin/carreras", "/api/admin/universidades", "/api/admin/preguntas"};
        for (String ruta : rutas) {
            mvcAdmin.perform(getConSesion(ruta, TOKEN_B)).andExpect(status().isForbidden());
        }
    }

    @Test
    @DisplayName("Un usuario normal no puede crear, editar ni borrar (y nada cambia)")
    void usuarioNormalNoPuedeModificar() throws Exception {
        mvcAdmin.perform(conSesion(conJson(post("/api/admin/carreras"), jsonCarrera("Intrusa", "ingenieria", 1)), TOKEN_B))
                .andExpect(status().isForbidden());
        mvcAdmin.perform(conSesion(conJson(put("/api/admin/carreras/1"), jsonCarrera("Cambiada", "ingenieria", 1)), TOKEN_B))
                .andExpect(status().isForbidden());
        mvcAdmin.perform(conSesion(delete("/api/admin/carreras/1"), TOKEN_B)).andExpect(status().isForbidden());

        org.assertj.core.api.Assertions.assertThat(repositorioAdmin.carreras).hasSize(1);
        org.assertj.core.api.Assertions.assertThat(repositorioAdmin.carreras.get(0).nombre()).isEqualTo("Ingeniería de prueba");
    }

    @Test
    @DisplayName("La cuenta administradora entra al resumen")
    void administradorPuedeEntrar() throws Exception {
        mvcAdmin.perform(getConSesion("/api/admin/resumen", TOKEN_A))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carreras", is(1)));
    }

    @Test
    @DisplayName("/api/yo/admin dice true solo para la cuenta administradora")
    void yoAdminDiceQuienEs() throws Exception {
        mvcAdmin.perform(getConSesion("/api/yo/admin", TOKEN_A))
                .andExpect(status().isOk()).andExpect(jsonPath("$.esAdmin", is(true)));
        mvcAdmin.perform(getConSesion("/api/yo/admin", TOKEN_B))
                .andExpect(status().isOk()).andExpect(jsonPath("$.esAdmin", is(false)));
    }

    @Test
    @DisplayName("Sin correo administrador configurado, nadie entra")
    void sinCorreoConfiguradoElPanelQuedaCerrado() throws Exception {
        MockMvc cerrado = construirMvc("");
        cerrado.perform(getConSesion("/api/admin/resumen", TOKEN_A)).andExpect(status().isForbidden());
        cerrado.perform(getConSesion("/api/yo/admin", TOKEN_A))
                .andExpect(status().isOk()).andExpect(jsonPath("$.esAdmin", is(false)));
    }

    @Test
    @DisplayName("Un usuario sin fila en la tabla usuarios no es administrador")
    void usuarioSinPerfilNoEsAdministrador() throws Exception {
        repositorioAdmin.correosPorId.remove(USUARIO_A);
        mvcAdmin.perform(getConSesion("/api/admin/resumen", TOKEN_A)).andExpect(status().isForbidden());
    }

    // ------------------------------------------------------------ carreras
    @Test
    @DisplayName("El administrador crea una carrera y aparece en el listado")
    void crearCarreraValida() throws Exception {
        mvcAdmin.perform(conSesion(conJson(post("/api/admin/carreras"), jsonCarrera("Ingeniería Nueva", "salud", 1)), TOKEN_A))
                .andExpect(status().isCreated());
        mvcAdmin.perform(getConSesion("/api/admin/carreras", TOKEN_A))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].nombre", is("Ingeniería Nueva")));
    }

    @Test
    @DisplayName("Datos invalidos de carrera: 400 con mensaje claro")
    void crearCarreraInvalida() throws Exception {
        mvcAdmin.perform(conSesion(conJson(post("/api/admin/carreras"), jsonCarrera("ab", "salud", 1)), TOKEN_A))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("nombre de la carrera")));
        mvcAdmin.perform(conSesion(conJson(post("/api/admin/carreras"), jsonCarrera("Carrera X", "inventada", 1)), TOKEN_A))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("área")));
        mvcAdmin.perform(conSesion(conJson(post("/api/admin/carreras"), jsonCarrera("Carrera X", "salud", 999)), TOKEN_A))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("universidad")));
        mvcAdmin.perform(conSesion(conJson(post("/api/admin/carreras"), "esto no es json"), TOKEN_A))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Editar una carrera que no existe responde 404")
    void editarCarreraInexistente() throws Exception {
        mvcAdmin.perform(conSesion(conJson(put("/api/admin/carreras/777"), jsonCarrera("Carrera X", "salud", 1)), TOKEN_A))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("No se puede borrar una carrera que ya tiene plan de estudios (409)")
    void noSeBorraCarreraConPlan() throws Exception {
        repositorioAdmin.semestresPorCarrera.put(1, 9);
        mvcAdmin.perform(conSesion(delete("/api/admin/carreras/1"), TOKEN_A)).andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Una carrera sin plan de estudios se borra (204)")
    void seBorraCarreraSinPlan() throws Exception {
        mvcAdmin.perform(conSesion(delete("/api/admin/carreras/1"), TOKEN_A)).andExpect(status().isNoContent());
        mvcAdmin.perform(getConSesion("/api/admin/carreras", TOKEN_A)).andExpect(jsonPath("$", hasSize(0)));
    }

    // ------------------------------------------------------------ universidades
    @Test
    @DisplayName("No se puede borrar una universidad con carreras asociadas (409)")
    void noSeBorraUniversidadConCarreras() throws Exception {
        mvcAdmin.perform(conSesion(delete("/api/admin/universidades/1"), TOKEN_A)).andExpect(status().isConflict());
    }

    @Test
    @DisplayName("El carácter de la universidad solo puede ser Pública o Privada")
    void validaCaracterDeUniversidad() throws Exception {
        String invalida = "{\"nombre\":\"Universidad Nueva\",\"municipio\":\"Medellín\",\"caracter\":\"Mixta\",\"tieneInformacionCompleta\":false}";
        String valida = "{\"nombre\":\"Universidad Nueva\",\"municipio\":\"Medellín\",\"caracter\":\"Pública\",\"tieneInformacionCompleta\":false}";
        mvcAdmin.perform(conSesion(conJson(post("/api/admin/universidades"), invalida), TOKEN_A))
                .andExpect(status().isBadRequest());
        mvcAdmin.perform(conSesion(conJson(post("/api/admin/universidades"), valida), TOKEN_A))
                .andExpect(status().isCreated());
    }

    // ------------------------------------------------------------ preguntas
    @Test
    @DisplayName("El texto de una pregunta se actualiza; vacio se rechaza")
    void editarTextoDePregunta() throws Exception {
        mvcAdmin.perform(conSesion(conJson(put("/api/admin/preguntas/1"), "{\"texto\":\"Texto nuevo de la pregunta\"}"), TOKEN_A))
                .andExpect(status().isNoContent());
        mvcAdmin.perform(conSesion(conJson(put("/api/admin/preguntas/1"), "{\"texto\":\"  \"}"), TOKEN_A))
                .andExpect(status().isBadRequest());
        mvcAdmin.perform(conSesion(conJson(put("/api/admin/preguntas/99"), "{\"texto\":\"Texto nuevo valido\"}"), TOKEN_A))
                .andExpect(status().isNotFound());
        mvcAdmin.perform(getConSesion("/api/admin/preguntas", TOKEN_A))
                .andExpect(jsonPath("$[0].texto", is("Texto nuevo de la pregunta")));
    }

    // ================================================================== falso
    /** Panel de administracion en memoria: un usuario, una universidad, una carrera y una pregunta. */
    private static final class RepositorioAdminFalso implements IRepositorioAdmin {

        final Map<String, String> correosPorId = new HashMap<>();
        final Map<Integer, Integer> semestresPorCarrera = new HashMap<>();
        final List<DatosAdmin.Carrera> carreras = new ArrayList<>();
        final List<DatosAdmin.Universidad> universidades = new ArrayList<>();
        final List<DatosAdmin.Pregunta> preguntas = new ArrayList<>();

        RepositorioAdminFalso() {
            universidades.add(new DatosAdmin.Universidad(1, "Universidad de prueba", "Privada", "Medellín", true));
            carreras.add(new DatosAdmin.Carrera(1, "Ingeniería de prueba", "Descripción de prueba larga",
                    "Campo laboral de prueba largo", "ingenieria", 9, new BigDecimal("1000000"), 1));
            preguntas.add(new DatosAdmin.Pregunta(1, "Pregunta de prueba", "intereses",
                    List.of(new DatosAdmin.Opcion(11, "Opción de prueba", "ingenieria", 2))));
        }

        @Override
        public Optional<String> obtenerCorreo(String usuarioId) {
            return Optional.ofNullable(correosPorId.get(usuarioId));
        }

        @Override
        public DatosAdmin.Resumen resumen() {
            return new DatosAdmin.Resumen(correosPorId.size(), 0, 0, 0, 0,
                    carreras.size(), universidades.size(), preguntas.size());
        }

        @Override
        public List<DatosAdmin.Usuario> listarUsuarios() {
            return List.of();
        }

        @Override
        public List<DatosAdmin.Pago> listarPagos() {
            return List.of();
        }

        @Override
        public List<DatosAdmin.Carrera> listarCarreras() {
            return List.copyOf(carreras);
        }

        @Override
        public void crearCarrera(DatosAdmin.Carrera carrera) {
            carreras.add(new DatosAdmin.Carrera(carreras.size() + 1, carrera.nombre(), carrera.descripcion(),
                    carrera.campoLaboral(), carrera.areaCategoria(), carrera.duracionSemestres(),
                    carrera.tarifaSemestre(), carrera.universidadId()));
        }

        @Override
        public boolean actualizarCarrera(int id, DatosAdmin.Carrera carrera) {
            for (int i = 0; i < carreras.size(); i++) {
                if (carreras.get(i).id() == id) {
                    carreras.set(i, carrera);
                    return true;
                }
            }
            return false;
        }

        @Override
        public int contarSemestresDeCarrera(int id) {
            return semestresPorCarrera.getOrDefault(id, 0);
        }

        @Override
        public boolean eliminarCarrera(int id) {
            return carreras.removeIf(carrera -> carrera.id() == id);
        }

        @Override
        public List<DatosAdmin.Universidad> listarUniversidades() {
            return List.copyOf(universidades);
        }

        @Override
        public boolean existeUniversidad(int id) {
            return universidades.stream().anyMatch(universidad -> universidad.id() == id);
        }

        @Override
        public void crearUniversidad(DatosAdmin.Universidad universidad) {
            universidades.add(universidad);
        }

        @Override
        public boolean actualizarUniversidad(int id, DatosAdmin.Universidad universidad) {
            return existeUniversidad(id);
        }

        @Override
        public int contarCarrerasDeUniversidad(int id) {
            return (int) carreras.stream().filter(carrera -> carrera.universidadId() == id).count();
        }

        @Override
        public boolean eliminarUniversidad(int id) {
            return universidades.removeIf(universidad -> universidad.id() == id);
        }

        @Override
        public List<DatosAdmin.Pregunta> listarPreguntas() {
            return List.copyOf(preguntas);
        }

        @Override
        public boolean actualizarTextoPregunta(int id, String texto) {
            for (int i = 0; i < preguntas.size(); i++) {
                DatosAdmin.Pregunta actual = preguntas.get(i);
                if (actual.id() == id) {
                    preguntas.set(i, new DatosAdmin.Pregunta(id, texto, actual.categoria(), actual.opciones()));
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean actualizarTextoOpcion(int id, String texto) {
            return false;
        }
    }
}
