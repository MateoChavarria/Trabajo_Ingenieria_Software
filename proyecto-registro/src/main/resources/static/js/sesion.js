/**
 * Utilidades de sesion compartidas por toda la aplicacion.
 * Requiere que js/utilidadesDom.js se cargue antes.
 *
 * Sprint 10: ademas de la sesion, aqui se maneja el "acceso completo"
 * (haber pagado). El servidor es quien decide de verdad; lo que se guarda
 * en el navegador solo sirve para dibujar candados sin esperar.
 */

const CLAVES_DATOS_USUARIO = ["sesion", "perfilVocacional", "carrerasRecomendadas", "afinidadPorArea", "carreraSeleccionada", "accesoCompleto", "esAdmin"];

/** Paginas internas a las que se puede volver despues del login o del pago (evita open redirect). */
const PAGINAS_PERMITIDAS_VOLVER = ["index.html", "explorar.html", "test.html", "perfil.html", "carreras.html", "carrera-detalle.html", "simulador.html"];

/** Paginas que ademas necesitan el parametro id (el id de la carrera). */
const PAGINAS_QUE_NECESITAN_ID = ["carrera-detalle.html", "simulador.html"];

function limpiarDatosSesion() {
    CLAVES_DATOS_USUARIO.forEach((clave) => localStorage.removeItem(clave));
}

/** Devuelve la sesion solo si existe, esta bien formada y no ha expirado. */
function obtenerSesion() {
    const sesion = leerJsonLocal("sesion");
    if (!sesion) return null;

    const expiraEn = Date.parse(sesion.fechaExpiracion);
    const valida = typeof sesion.tokenAcceso === "string" && sesion.tokenAcceso.length > 0
        && typeof sesion.idUsuario === "string" && sesion.idUsuario.length > 0
        && Number.isFinite(expiraEn) && Date.now() < expiraEn;

    if (!valida) {
        limpiarDatosSesion();
        return null;
    }
    return sesion;
}

function cerrarSesion() {
    limpiarDatosSesion();
    window.location.href = "index.html";
}

// ============================================================
// Destinos seguros (login y pago)
// ============================================================

function paginaActual() {
    return window.location.pathname.split("/").pop() || "index.html";
}

function idCarreraEsValido(valor) {
    return /^[1-9][0-9]{0,8}$/.test(valor || "");
}

/** Valida "volver" (y el id de la carrera si hace falta) contra una lista blanca de paginas propias. */
function resolverDestino(volver, id, porDefecto) {
    if (!PAGINAS_PERMITIDAS_VOLVER.includes(volver)) return porDefecto;
    if (PAGINAS_QUE_NECESITAN_ID.includes(volver)) {
        return idCarreraEsValido(id) ? volver + "?id=" + id : "explorar.html";
    }
    return volver;
}

function destinoSeguroDespuesDeLogin(volver, id) {
    return resolverDestino(volver, id, "perfil.html");
}

/** Despues de pagar siempre se vuelve a la pagina principal (no se manda directo al test). */
function destinoSeguroDespuesDePago() {
    return "index.html";
}

/** Texto "&volver=...&id=..." para pasar el destino de una pantalla de pago a la siguiente. Vacio si no hay destino valido. */
function sufijoVolver() {
    const parametros = new URLSearchParams(window.location.search);
    const volver = parametros.get("volver");
    if (!PAGINAS_PERMITIDAS_VOLVER.includes(volver)) return "";

    let sufijo = "&volver=" + encodeURIComponent(volver);
    const id = parametros.get("id");
    if (PAGINAS_QUE_NECESITAN_ID.includes(volver) && idCarreraEsValido(id)) {
        sufijo += "&id=" + encodeURIComponent(id);
    }
    return sufijo;
}

/** Devuelve la pagina con el mismo destino de regreso que tiene la URL actual (para encadenar pantallas de pago). */
function enlaceConVolver(pagina) {
    const sufijo = sufijoVolver();
    return sufijo ? pagina + "?" + sufijo.slice(1) : pagina;
}

/** Arma "?volver=pagina&id=N" para la pagina en la que estamos (el id solo si la pagina lo necesita). */
function consultaVolverDeEstaPagina() {
    const pagina = paginaActual();
    if (!PAGINAS_PERMITIDAS_VOLVER.includes(pagina)) return "";

    let consulta = "?volver=" + encodeURIComponent(pagina);
    const id = new URLSearchParams(window.location.search).get("id");
    if (PAGINAS_QUE_NECESITAN_ID.includes(pagina) && idCarreraEsValido(id)) {
        consulta += "&id=" + encodeURIComponent(id);
    }
    return consulta;
}

function redirigirALogin() {
    window.location.href = "login.html" + consultaVolverDeEstaPagina();
}

function redirigirAPago() {
    window.location.href = "pago.html" + consultaVolverDeEstaPagina();
}

/**
 * Bloquea el acceso a paginas que requieren sesion (test, perfil,
 * listado de recomendaciones). Si no hay sesion, redirige al login
 * guardando a donde volver una vez inicie sesion.
 */
function requerirSesion() {
    const sesion = obtenerSesion();
    if (!sesion) {
        redirigirALogin();
        return null;
    }
    return sesion;
}

// ============================================================
// Acceso completo (pago)
// ============================================================

function guardarAcceso(valor) {
    const sesion = obtenerSesion();
    if (!sesion) return;
    try {
        localStorage.setItem("accesoCompleto", JSON.stringify({ idUsuario: sesion.idUsuario, valor: valor }));
    } catch (error) {
        // Sin almacenamiento disponible: la pagina funciona igual, solo consulta al servidor cada vez.
    }
}

/** true / false segun lo ultimo que dijo el servidor, o null si todavia no se sabe. */
function leerAccesoGuardado() {
    const sesion = obtenerSesion();
    const datos = leerJsonLocal("accesoCompleto");
    if (!sesion || !datos || datos.idUsuario !== sesion.idUsuario || typeof datos.valor !== "boolean") {
        return null;
    }
    return datos.valor;
}

// ============================================================
// Panel de administracion (el boton es solo visual: el servidor decide)
// ============================================================

function leerEsAdminGuardado() {
    const sesion = obtenerSesion();
    const datos = leerJsonLocal("esAdmin");
    return Boolean(sesion && datos && datos.idUsuario === sesion.idUsuario && datos.valor === true);
}

/** Pregunta al servidor si la cuenta es la administradora. Devuelve true solo si cambio lo que se sabia. */
async function consultarSiEsAdmin() {
    const sesion = obtenerSesion();
    if (!sesion) return false;

    try {
        const respuesta = await fetch("/api/yo/admin", {
            headers: { "Authorization": "Bearer " + sesion.tokenAcceso }
        });
        if (!respuesta.ok) return false;

        const esAdmin = (await respuesta.json()).esAdmin === true;
        const habiaCambio = esAdmin !== leerEsAdminGuardado();
        localStorage.setItem("esAdmin", JSON.stringify({ idUsuario: sesion.idUsuario, valor: esAdmin }));
        return habiaCambio;
    } catch (error) {
        return false;
    }
}

/**
 * Pregunta al servidor si el usuario ya pago y guarda la respuesta.
 * Devuelve true, false, o null si no se pudo saber (sin sesion o sin
 * conexion). No redirige: quien la llama decide que hacer.
 */
async function consultarAcceso() {
    const sesion = obtenerSesion();
    if (!sesion) return null;

    try {
        const respuesta = await fetch("/api/pagos/oferta", {
            headers: { "Authorization": "Bearer " + sesion.tokenAcceso }
        });
        if (!respuesta.ok) return null;

        const oferta = await respuesta.json();
        const acceso = oferta.accesoCompleto === true;
        guardarAcceso(acceso);
        return acceso;
    } catch (error) {
        return null;
    }
}

/**
 * fetch() que adjunta el token de sesion en la cabecera Authorization.
 * Si el servidor responde 401 (token invalido o expirado), se cierra la
 * sesion local y se envia al usuario al login. Si responde 403 (la
 * funcion requiere acceso completo), se envia a la pantalla de pago.
 */
async function fetchAutenticado(url, opciones = {}) {
    const sesion = obtenerSesion();
    if (!sesion) {
        redirigirALogin();
        throw new Error("Sesión no válida");
    }

    const cabeceras = new Headers(opciones.headers || {});
    cabeceras.set("Authorization", "Bearer " + sesion.tokenAcceso);

    const respuesta = await fetch(url, { ...opciones, headers: cabeceras });
    if (respuesta.status === 401) {
        limpiarDatosSesion();
        redirigirALogin();
        throw new Error("Sesión expirada");
    }
    if (respuesta.status === 403) {
        guardarAcceso(false);
        redirigirAPago();
        throw new Error("Acceso completo requerido");
    }
    return respuesta;
}

/**
 * Dibuja la barra de navegacion superior (igual en todas las
 * paginas), cambiando los links segun si hay sesion iniciada y si el
 * usuario ya pago (candado). Solo usa contenido estatico, sin datos
 * del usuario.
 */
function pintarNav() {
    const contenedor = document.getElementById("nav");
    if (!contenedor) return;

    const sesion = obtenerSesion();
    const bloqueado = sesion ? leerAccesoGuardado() !== true : false;

    const linkCuenta = sesion
        ? `<a href="perfil.html">Mi perfil</a><a href="#" id="linkCerrarSesion">Cerrar sesión</a>`
        : `<a href="login.html">Iniciar sesión</a><a class="btn sun" href="registro.html">Registrarme</a>`;

    let linkTest = "";
    if (sesion) {
        linkTest = bloqueado
            ? `<a href="pago.html?volver=test.html" title="Se desbloquea con el acceso completo">🔒 Hacer el test</a>`
            : `<a href="test.html">Hacer el test</a>`;
    }
    const botonAdmin = sesion && leerEsAdminGuardado()
        ? `<a class="btn outline" href="admin.html">Administración</a>` : "";
    const botonDesbloquear = bloqueado ? `<a class="btn sun" href="pago.html">Desbloquear acceso</a>` : "";

    contenedor.innerHTML = `
        <div class="wrap">
            <a class="logo" href="index.html"><i><b></b><b></b><b></b><b></b></i>Orientador Vocacional</a>
            <nav class="nav-links">
                <a href="explorar.html">Explorar carreras</a>
                <a href="universidades.html">Universidades</a>
                ${linkTest}
                ${linkCuenta}
                ${botonDesbloquear}
                ${botonAdmin}
            </nav>
        </div>
    `;

    const botonCerrar = document.getElementById("linkCerrarSesion");
    if (botonCerrar) {
        botonCerrar.addEventListener("click", (evento) => {
            evento.preventDefault();
            cerrarSesion();
        });
    }
}

// Inicializacion comun (antes era un <script> inline en cada pagina, lo
// que impedia usar una Content-Security-Policy estricta).
if (document.body.hasAttribute("data-requiere-sesion")) {
    requerirSesion();
}

// Login y registro son solo para quien NO tiene sesion. Si ya hay una, se
// explica que primero debe cerrarla para entrar con otra cuenta o crear una nueva.
if (document.body.hasAttribute("data-solo-invitados") && obtenerSesion()) {
    const tarjetaFormulario = document.querySelector(".form-card");
    if (tarjetaFormulario) {
        Array.from(tarjetaFormulario.children).forEach((hijo) => { hijo.hidden = true; });

        const irAlPerfil = crearElemento("a", "btn sun block", "Ir a mi perfil");
        irAlPerfil.href = "perfil.html";
        const cerrar = crearElemento("button", "btn outline block", "Cerrar sesión");
        cerrar.type = "button";
        cerrar.style.marginTop = "12px";
        cerrar.addEventListener("click", cerrarSesion);

        const aviso = crearElemento("div");
        aviso.append(
            crearElemento("h2", null, "Ya tienes una sesión iniciada"),
            crearElemento("p", "sub", "Para entrar con otra cuenta o crear una nueva, primero cierra la sesión actual."),
            irAlPerfil,
            cerrar
        );
        tarjetaFormulario.appendChild(aviso);
    }
}

// Una sola consulta al servidor por pagina: la usan el menu, el bloqueo de
// paginas de pago y los scripts de cada pagina (con esperarAcceso()).
// Si ya sabemos que este usuario pago (el pago es definitivo), no se espera la
// respuesta del servidor para mostrar la pagina: se confirma en segundo plano.
const verificacionEnSegundoPlano = obtenerSesion() ? consultarAcceso() : Promise.resolve(null);
const consultaInicialAcceso = leerAccesoGuardado() === true
    ? Promise.resolve(true)
    : verificacionEnSegundoPlano;

function esperarAcceso() {
    return consultaInicialAcceso;
}

pintarNav();

if (obtenerSesion()) {
    consultarSiEsAdmin().then((cambio) => { if (cambio) pintarNav(); });
}

consultaInicialAcceso.then((acceso) => {
    // Paginas que solo se pueden ver pagando: se ocultan hasta confirmar el acceso.
    if (document.body.hasAttribute("data-requiere-acceso") && obtenerSesion()) {
        if (acceso === false) {
            redirigirAPago();
            return;
        }
        // acceso === null (sin conexion): se deja ver; el servidor igual bloquea los datos.
        document.body.classList.add("acceso-verificado");
    }
    if (acceso !== null) pintarNav();
});
