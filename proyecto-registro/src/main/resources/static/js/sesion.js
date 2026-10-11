/**
 * Utilidades de sesion compartidas por toda la aplicacion.
 * Requiere que js/utilidadesDom.js se cargue antes.
 */

const CLAVES_DATOS_USUARIO = ["sesion", "perfilVocacional", "carrerasRecomendadas", "afinidadPorArea", "carreraSeleccionada"];

/** Paginas internas a las que se puede volver despues del login (evita open redirect). */
const PAGINAS_PERMITIDAS_VOLVER = ["index.html", "explorar.html", "test.html", "perfil.html", "carreras.html", "carrera-detalle.html"];

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

function redirigirALogin() {
    const paginaActual = window.location.pathname.split("/").pop();
    window.location.href = "login.html?volver=" + encodeURIComponent(paginaActual);
}

/** Valida el parametro "volver" contra una lista blanca de paginas propias. */
function destinoSeguroDespuesDeLogin(valor) {
    return PAGINAS_PERMITIDAS_VOLVER.includes(valor) ? valor : "test.html";
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

/**
 * fetch() que adjunta el token de sesion en la cabecera Authorization.
 * Si el servidor responde 401 (token invalido o expirado), se cierra la
 * sesion local y se envia al usuario al login.
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
    return respuesta;
}

/**
 * Dibuja la barra de navegacion superior (igual en todas las
 * paginas), cambiando los links segun si hay sesion iniciada.
 * Solo usa contenido estatico, sin datos del usuario.
 */
function pintarNav() {
    const contenedor = document.getElementById("nav");
    if (!contenedor) return;

    const sesion = obtenerSesion();

    const linkCuenta = sesion
        ? `<a href="perfil.html">Mi perfil</a><a href="#" id="linkCerrarSesion">Cerrar sesión</a>`
        : `<a href="login.html">Iniciar sesión</a><a class="btn sun" href="registro.html">Registrarme</a>`;

    contenedor.innerHTML = `
        <div class="wrap">
            <a class="logo" href="index.html"><i><b></b><b></b><b></b><b></b></i>Orientador UPB</a>
            <nav class="nav-links">
                <a href="explorar.html">Explorar carreras</a>
                ${sesion ? `<a href="test.html">Hacer el test</a>` : ""}
                ${linkCuenta}
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
pintarNav();