package com.orientadorvocacional.integracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orientadorvocacional.controlador.AuthController;
import com.orientadorvocacional.controlador.CarreraController;
import com.orientadorvocacional.controlador.ManejadorGlobalErrores;
import com.orientadorvocacional.controlador.PagoController;
import com.orientadorvocacional.controlador.PensumController;
import com.orientadorvocacional.controlador.PerfilController;
import com.orientadorvocacional.controlador.SimulacionController;
import com.orientadorvocacional.controlador.TestController;
import com.orientadorvocacional.controlador.UniversidadController;
import com.orientadorvocacional.excepciones.CarreraNoEncontradaException;
import com.orientadorvocacional.excepciones.ConsultaCarrerasFallidaException;
import com.orientadorvocacional.excepciones.ConsultaPensumFallidaException;
import com.orientadorvocacional.excepciones.ConsultaTemasDificilesFallidaException;
import com.orientadorvocacional.excepciones.ConsultaUniversidadesFallidaException;
import com.orientadorvocacional.excepciones.CorreoYaRegistradoException;
import com.orientadorvocacional.excepciones.CredencialesInvalidasException;
import com.orientadorvocacional.excepciones.InicioSesionFallidoException;
import com.orientadorvocacional.excepciones.OperacionPagoFallidaException;
import com.orientadorvocacional.excepciones.RegistroFallidoException;
import com.orientadorvocacional.excepciones.ResultadoTestFallidoException;
import com.orientadorvocacional.modelo.Carrera;
import com.orientadorvocacional.modelo.EstadoPago;
import com.orientadorvocacional.modelo.MateriaPensum;
import com.orientadorvocacional.modelo.OpcionRespuesta;
import com.orientadorvocacional.modelo.Pago;
import com.orientadorvocacional.modelo.PensumCarrera;
import com.orientadorvocacional.modelo.Pregunta;
import com.orientadorvocacional.modelo.ResultadoTest;
import com.orientadorvocacional.modelo.SemestrePensum;
import com.orientadorvocacional.modelo.SesionUsuario;
import com.orientadorvocacional.modelo.Universidad;
import com.orientadorvocacional.modelo.Usuario;
import com.orientadorvocacional.repositorio.IPensumRepositorio;
import com.orientadorvocacional.repositorio.IRepositorioAcceso;
import com.orientadorvocacional.repositorio.IRepositorioPagos;
import com.orientadorvocacional.repositorio.IRepositorioPreguntas;
import com.orientadorvocacional.repositorio.IRepositorioResultadoTest;
import com.orientadorvocacional.repositorio.IRepositorioUsuario;
import com.orientadorvocacional.repositorio.ICarreraRepositorio;
import com.orientadorvocacional.repositorio.ITemasDificilesRepositorio;
import com.orientadorvocacional.repositorio.IUniversidadRepositorio;
import com.orientadorvocacional.seguridad.InterceptorAccesoCompleto;
import com.orientadorvocacional.seguridad.InterceptorAutenticacion;
import com.orientadorvocacional.servicio.CasoUsoCalcularYGuardarResultadoVocacional;
import com.orientadorvocacional.servicio.CasoUsoCancelarPago;
import com.orientadorvocacional.servicio.CasoUsoConsultarPago;
import com.orientadorvocacional.servicio.CasoUsoIniciarPago;
import com.orientadorvocacional.servicio.CasoUsoIniciarSesion;
import com.orientadorvocacional.servicio.CasoUsoListarTodasLasCarreras;
import com.orientadorvocacional.servicio.CasoUsoListarUniversidades;
import com.orientadorvocacional.servicio.CasoUsoObtenerCarrerasRecomendadas;
import com.orientadorvocacional.servicio.CasoUsoObtenerCuestionario;
import com.orientadorvocacional.servicio.CasoUsoObtenerDetalleCarrera;
import com.orientadorvocacional.servicio.CasoUsoObtenerHistorialTest;
import com.orientadorvocacional.servicio.CasoUsoObtenerOfertaAcceso;
import com.orientadorvocacional.servicio.CasoUsoObtenerPensumCarrera;
import com.orientadorvocacional.servicio.CasoUsoObtenerPerfilVocacional;
import com.orientadorvocacional.servicio.CasoUsoProcesarPago;
import com.orientadorvocacional.servicio.CasoUsoRegistrarUsuario;
import com.orientadorvocacional.servicio.CasoUsoSimularCarrera;
import com.orientadorvocacional.servicio.IServicioAutenticacion;
import com.orientadorvocacional.servicio.PasarelaSimulada;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Base comun de las pruebas de INTEGRACION.
 *
 * Que se integra aqui: las peticiones HTTP entran por MockMvc y recorren
 * el camino real de la aplicacion: interceptores de seguridad, controladores
 * REAL, casos de uso REAL, modelo con sus validaciones y el manejador global
 * de errores. Lo unico "falso" es lo que esta en los extremos y necesita
 * internet o base de datos: Supabase Auth y las tablas de Postgres. Se
 * reemplazan por versiones en memoria (clases internas de este archivo).
 *
 * Por que no usamos @SpringBootTest: arrancaria Spring completo y pediria la
 * conexion a Supabase, que no existe en GitHub Actions (no hay secretos
 * ahi). Con standaloneSetup las pruebas son rapidas y no necesitan nada.
 *
 * Cada prueba empieza con datos limpios (@BeforeEach crea todo de nuevo), asi
 * que ninguna depende del orden de las demas.
 */
abstract class IntegracionBase {

    // ---- Usuarios de prueba (sus tokens los reconoce ServicioAutenticacionFalso) ----
    protected static final String USUARIO_A = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";
    protected static final String USUARIO_B = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb";
    protected static final String TOKEN_A = "token-de-a";
    protected static final String TOKEN_B = "token-de-b";

    /** Mismas rutas que ConfiguracionSeguridad (se repiten aqui para armar el MockMvc). */
    private static final String[] RUTAS_CON_SESION = {
            "/api/test/preguntas", "/api/test/resultado", "/api/perfil/**",
            "/api/carreras/recomendadas", "/api/carreras/*/simulacion", "/api/pagos/**"
    };
    private static final String[] RUTAS_DE_PAGO = {
            "/api/test/preguntas", "/api/test/resultado", "/api/perfil/resumen",
            "/api/carreras/recomendadas", "/api/carreras/*/simulacion"
    };

    protected static final ObjectMapper JSON = new ObjectMapper();

    protected MockMvc mvc;

    // Piezas falsas que cada prueba puede consultar o preparar.
    protected ServicioAutenticacionFalso autenticacion;
    protected RepositorioUsuarioFalso usuarios;
    protected RepositorioResultadosFalso resultados;
    protected RepositorioAccesoFalso acceso;
    protected RepositorioPagosFalso pagos;
    protected CatalogoFalso catalogo;

    @BeforeEach
    void montarAplicacion() {
        autenticacion = new ServicioAutenticacionFalso();
        autenticacion.registrarToken(TOKEN_A, USUARIO_A);
        autenticacion.registrarToken(TOKEN_B, USUARIO_B);

        usuarios = new RepositorioUsuarioFalso();
        resultados = new RepositorioResultadosFalso();
        acceso = new RepositorioAccesoFalso();
        pagos = new RepositorioPagosFalso();
        catalogo = new CatalogoFalso();

        // ---- Casos de uso REALES, conectados a los repositorios falsos ----
        var obtenerPensum = new CasoUsoObtenerPensumCarrera(catalogo, catalogo.universidadesRepo, catalogo);
        var casoPerfil = new CasoUsoObtenerPerfilVocacional();

        var auth = new AuthController(
                new CasoUsoRegistrarUsuario(autenticacion, usuarios),
                new CasoUsoIniciarSesion(autenticacion));
        var test = new TestController(
                new CasoUsoObtenerCuestionario(catalogo),
                new CasoUsoCalcularYGuardarResultadoVocacional(catalogo, resultados));
        var perfilController = new PerfilController(
                casoPerfil, new CasoUsoObtenerHistorialTest(resultados, casoPerfil));
        var carreras = new CarreraController(
                new CasoUsoObtenerCarrerasRecomendadas(catalogo),
                new CasoUsoObtenerDetalleCarrera(catalogo),
                new CasoUsoListarTodasLasCarreras(catalogo));
        var pensum = new PensumController(obtenerPensum);
        var simulacion = new SimulacionController(new CasoUsoSimularCarrera(obtenerPensum, catalogo));
        var universidades = new UniversidadController(new CasoUsoListarUniversidades(catalogo.universidadesRepo));
        var pago = new PagoController(
                new CasoUsoObtenerOfertaAcceso(acceso),
                new CasoUsoIniciarPago(acceso, pagos),
                new CasoUsoProcesarPago(pagos, acceso, new PasarelaSimulada()),
                new CasoUsoCancelarPago(pagos),
                new CasoUsoConsultarPago(pagos));

        var convertidorTexto = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        var convertidorJson = new MappingJackson2HttpMessageConverter();

        mvc = MockMvcBuilders
                .standaloneSetup(auth, test, perfilController, carreras, pensum, simulacion, universidades, pago)
                .setControllerAdvice(new ManejadorGlobalErrores())
                .setMessageConverters(convertidorTexto, convertidorJson)
                // Orden igual al de ConfiguracionSeguridad: 1) sesion, 2) pago.
                .addMappedInterceptors(RUTAS_CON_SESION, new InterceptorAutenticacion(autenticacion))
                .addMappedInterceptors(RUTAS_DE_PAGO, new InterceptorAccesoCompleto(acceso))
                .build();
    }

    // ------------------------------------------------------------------
    // Ayudas para escribir peticiones cortas
    // ------------------------------------------------------------------

    protected MockHttpServletRequestBuilder conSesion(MockHttpServletRequestBuilder peticion, String token) {
        return peticion.header("Authorization", "Bearer " + token);
    }

    protected MockHttpServletRequestBuilder postJson(String ruta, Object cuerpo) throws Exception {
        return post(ruta).contentType(MediaType.APPLICATION_JSON).content(JSON.writeValueAsString(cuerpo));
    }

    protected MockHttpServletRequestBuilder postJsonTexto(String ruta, String json) {
        return post(ruta).contentType(MediaType.APPLICATION_JSON).content(json);
    }

    protected MockHttpServletRequestBuilder getConSesion(String ruta, String token) {
        return conSesion(get(ruta), token);
    }

    protected String cuerpo(MvcResult resultado) throws Exception {
        return resultado.getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    /** Respuestas de un usuario que contesta SIEMPRE la opcion de un area (ver CatalogoFalso). */
    protected Map<String, Object> respuestasSiempre(String area) {
        List<Map<String, Integer>> lista = new ArrayList<>();
        for (Pregunta pregunta : catalogo.preguntas) {
            for (OpcionRespuesta opcion : pregunta.getOpciones()) {
                if (opcion.getArea().equals(area)) {
                    lista.add(Map.of("preguntaId", pregunta.getId(), "opcionId", opcion.getId()));
                }
            }
        }
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("respuestas", lista);
        return cuerpo;
    }

    /** Deja a USUARIO_A con acceso completo (como si ya hubiera pagado). */
    protected void darAccesoAUsuarioA() throws Exception {
        acceso.otorgarAccesoCompleto(USUARIO_A);
    }

    // ==================================================================
    // FALSOS EN MEMORIA (reemplazan a Supabase y a Postgres)
    // ==================================================================

    /** Supabase Auth en memoria: guarda correos, contrasenas y tokens. */
    protected static final class ServicioAutenticacionFalso implements IServicioAutenticacion {

        private final Map<String, String> contrasenaPorCorreo = new HashMap<>();
        private final Map<String, String> idPorCorreo = new HashMap<>();
        private final Map<String, String> idPorToken = new HashMap<>();
        /** Para simular que el proveedor esta caido. */
        public boolean proveedorCaido = false;
        private int contador = 0;

        void registrarToken(String token, String idUsuario) {
            idPorToken.put(token, idUsuario);
        }

        @Override
        public String registrar(Usuario usuario) throws CorreoYaRegistradoException, RegistroFallidoException {
            if (proveedorCaido) {
                throw new RegistroFallidoException("Proveedor caido.");
            }
            String correo = usuario.getCorreo().toLowerCase();
            if (contrasenaPorCorreo.containsKey(correo)) {
                throw new CorreoYaRegistradoException("Este correo ya está registrado.");
            }
            String id = "id-nuevo-" + (++contador);
            contrasenaPorCorreo.put(correo, usuario.getContrasena());
            idPorCorreo.put(correo, id);
            return id;
        }

        @Override
        public SesionUsuario iniciarSesion(String correo, String contrasena)
                throws CredencialesInvalidasException, InicioSesionFallidoException {
            if (proveedorCaido) {
                throw new InicioSesionFallidoException("Proveedor caido.");
            }
            String guardada = contrasenaPorCorreo.get(correo.toLowerCase());
            // Mismo mensaje si falla el correo o la contrasena (como hace Supabase).
            if (guardada == null || !guardada.equals(contrasena)) {
                throw new CredencialesInvalidasException("Correo o contraseña incorrectos.");
            }
            String id = idPorCorreo.get(correo.toLowerCase());
            String token = "token-" + id;
            idPorToken.put(token, id);
            return new SesionUsuario(token, id);
        }

        @Override
        public String validarToken(String tokenAcceso)
                throws CredencialesInvalidasException, InicioSesionFallidoException {
            if (proveedorCaido) {
                throw new InicioSesionFallidoException("Proveedor caido.");
            }
            String id = idPorToken.get(tokenAcceso);
            if (id == null) {
                throw new CredencialesInvalidasException("Token invalido.");
            }
            return id;
        }
    }

    /** Tabla "usuarios" en memoria: solo anota cuantos perfiles se guardaron. */
    protected static final class RepositorioUsuarioFalso implements IRepositorioUsuario {
        public final Map<String, Usuario> guardados = new LinkedHashMap<>();

        @Override
        public void guardar(String id, Usuario usuario) {
            guardados.put(id, usuario);
        }
    }

    /** Tabla "resultados_test" en memoria. El historial sale del mas reciente al mas antiguo. */
    protected static final class RepositorioResultadosFalso implements IRepositorioResultadoTest {
        public final List<ResultadoTest> filas = new ArrayList<>();

        @Override
        public void guardar(ResultadoTest resultado) throws ResultadoTestFallidoException {
            filas.add(resultado);
        }

        @Override
        public List<ResultadoTest> obtenerPorUsuario(String usuarioId) {
            List<ResultadoTest> propios = new ArrayList<>();
            for (int i = filas.size() - 1; i >= 0; i--) {
                if (filas.get(i).getUsuarioId().equals(usuarioId)) {
                    propios.add(filas.get(i));
                }
            }
            return propios;
        }
    }

    /** Columna usuarios.acceso_completo en memoria. */
    protected static final class RepositorioAccesoFalso implements IRepositorioAcceso {
        private final java.util.Set<String> conAcceso = new java.util.HashSet<>();

        @Override
        public boolean tieneAccesoCompleto(String usuarioId) throws OperacionPagoFallidaException {
            return conAcceso.contains(usuarioId);
        }

        @Override
        public void otorgarAccesoCompleto(String usuarioId) throws OperacionPagoFallidaException {
            conAcceso.add(usuarioId);
        }
    }

    /** Tabla "pagos" en memoria. registrarResolucion solo acepta pagos aun pendientes, como el UPDATE real. */
    protected static final class RepositorioPagosFalso implements IRepositorioPagos {
        private final Map<String, Pago> filas = new LinkedHashMap<>();

        public int total() {
            return filas.size();
        }

        @Override
        public void guardarNuevo(Pago pago) {
            filas.put(pago.getReferencia(), copiar(pago));
        }

        @Override
        public Optional<Pago> buscarPorReferencia(String referencia, String usuarioId) {
            Pago fila = filas.get(referencia);
            if (fila == null || !fila.getUsuarioId().equals(usuarioId)) {
                return Optional.empty();
            }
            return Optional.of(copiar(fila));
        }

        @Override
        public Optional<Pago> buscarPendienteDeUsuario(String usuarioId) {
            return filas.values().stream()
                    .filter(p -> p.getUsuarioId().equals(usuarioId) && p.getEstado() == EstadoPago.PENDIENTE)
                    .findFirst()
                    .map(this::copiar);
        }

        @Override
        public boolean registrarResolucion(Pago pago) {
            Pago fila = filas.get(pago.getReferencia());
            if (fila == null || fila.getEstado() != EstadoPago.PENDIENTE) {
                return false;
            }
            filas.put(pago.getReferencia(), copiar(pago));
            return true;
        }

        private Pago copiar(Pago p) {
            return new Pago(p.getUsuarioId(), p.getMonto(), p.getReferencia(), p.getEstado(),
                    p.getFechaCreacion(), p.getFechaResolucion(), p.getMotivoFallo());
        }
    }

    /**
     * Datos de catalogo en memoria: cuestionario, carreras, universidades y pensum.
     * Implementa de una vez cuatro interfaces de lectura; las universidades van aparte
     * (universidadesRepo) porque su metodo obtenerTodas() choca con el de carreras.
     *
     * Datos de prueba (los valores estan elegidos para que las cuentas sean faciles):
     *  - 3 preguntas; cada una tiene una opcion de "ingenieria", una de "salud" y una de "administracion".
     *  - Carreras: 1 Ingenieria de Sistemas (ingenieria, UPB), 2 Enfermeria (salud, UPB),
     *    3 Administración de Empresas (administracion, otra universidad), 4 Ingenieria Civil (ingenieria, UPB).
     */
    protected static final class CatalogoFalso implements IRepositorioPreguntas, ICarreraRepositorio,
            IPensumRepositorio, ITemasDificilesRepositorio {

        public final List<Pregunta> preguntas = new ArrayList<>();
        public final List<Carrera> carreras = new ArrayList<>();
        public final List<Universidad> universidades = new ArrayList<>();
        /** Ids de las carreras que pertenecen a una universidad con informacion completa (UPB). */
        public final java.util.Set<Integer> carrerasUpb = new java.util.HashSet<>();
        /** Para simular que la consulta de universidades falla. */
        public boolean universidadesCaidas = false;

        public final IUniversidadRepositorio universidadesRepo = new IUniversidadRepositorio() {
            @Override
            public List<Universidad> obtenerTodas() throws ConsultaUniversidadesFallidaException {
                if (universidadesCaidas) {
                    throw new ConsultaUniversidadesFallidaException("No se pudo consultar las universidades.");
                }
                return universidades;
            }

            @Override
            public boolean carreraPerteneceAUniversidadConInformacionCompleta(int carreraId) {
                return carrerasUpb.contains(carreraId);
            }
        };

        CatalogoFalso() {
            int idOpcion = 100;
            for (int p = 1; p <= 3; p++) {
                preguntas.add(new Pregunta(p, "Pregunta " + p, "intereses", List.of(
                        new OpcionRespuesta(idOpcion++, "Opción ingeniería " + p, "ingenieria", 3),
                        new OpcionRespuesta(idOpcion++, "Opción salud " + p, "salud", 3),
                        new OpcionRespuesta(idOpcion++, "Opción administracion " + p, "administracion", 3))));
            }

            carreras.add(carrera(1, "Ingeniería de Sistemas", "ingenieria"));
            carreras.add(carrera(2, "Enfermería", "salud"));
            carreras.add(carrera(3, "Administración de Empresas", "administracion"));
            carreras.add(carrera(4, "Ingeniería Civil", "ingenieria"));
            carrerasUpb.addAll(List.of(1, 2, 4));

            // Ya en orden alfabetico, como las entrega la consulta real (findAllByOrderByNombreAsc).
            universidades.add(new Universidad(1, "Universidad de Antioquia", "Pública", "Medellín", false));
            universidades.add(new Universidad(2, "Universidad EAFIT", "Privada", "Medellín", false));
            universidades.add(new Universidad(3, "Universidad Pontificia Bolivariana", "Privada", "Medellín", true));
        }

        private static Carrera carrera(int id, String nombre, String area) {
            return new Carrera(id, nombre, "Descripción de " + nombre, "Campo laboral de " + nombre,
                    area, 10, new BigDecimal("9000000"));
        }

        // ---- IRepositorioPreguntas ----
        @Override
        public List<Pregunta> obtenerCuestionarioCompleto() {
            return preguntas;
        }

        // ---- ICarreraRepositorio ----
        @Override
        public List<Carrera> obtenerTodas() throws ConsultaCarrerasFallidaException {
            return carreras;
        }

        @Override
        public Carrera obtenerPorId(int id) throws CarreraNoEncontradaException {
            return carreras.stream().filter(c -> c.getId() == id).findFirst()
                    .orElseThrow(() -> new CarreraNoEncontradaException("No existe una carrera con id " + id + "."));
        }

        // ---- IPensumRepositorio ----
        @Override
        public PensumCarrera obtenerPorCarrera(Carrera carrera) throws ConsultaPensumFallidaException {
            List<SemestrePensum> semestres = List.of(
                    new SemestrePensum(1, "Fundamentos", "Primer semestre", List.of(
                            new MateriaPensum("Cálculo Diferencial", 4, "Matemáticas"),
                            new MateriaPensum("Programación I", 3, "Programación"))),
                    new SemestrePensum(2, "Bases", "Segundo semestre", List.of(
                            new MateriaPensum("Cálculo Integral", 4, "Matemáticas"))));
            return new PensumCarrera(carrera.getId(), carrera.getNombre(), semestres);
        }

        // ---- ITemasDificilesRepositorio ----
        @Override
        public Map<Integer, Map<String, List<String>>> obtenerPorCarrera(int carreraId)
                throws ConsultaTemasDificilesFallidaException {
            return Map.of(1, Map.of("Cálculo Diferencial", List.of("Límites", "Derivadas")));
        }
    }
}
