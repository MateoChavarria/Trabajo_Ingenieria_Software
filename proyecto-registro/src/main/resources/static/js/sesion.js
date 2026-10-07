/**
 * Utilidades de sesion compartidas por toda la aplicacion.
 */

function obtenerSesion() {
    const datos = localStorage.getItem("sesion");
    return datos ? JSON.parse(datos) : null;
}

function cerrarSesion() {
    localStorage.removeItem("sesion");
    localStorage.removeItem("perfilVocacional");
    localStorage.removeItem("carrerasRecomendadas");
    localStorage.removeItem("afinidadPorArea");
    localStorage.removeItem("carreraSeleccionada");
    window.location.href = "index.html";
}

/**
 * Bloquea el acceso a paginas que requieren sesion (test, perfil,
 * listado de recomendaciones). Si no hay sesion, redirige al login
 * guardando a donde volver una vez inicie sesion.
 */
function requerirSesion() {
    const sesion = obtenerSesion();
    if (!sesion) {
        const paginaActual = window.location.pathname.split("/").pop();
        window.location.href = "login.html?volver=" + encodeURIComponent(paginaActual);
        return null;
    }
    return sesion;
}

/**
 * Dibuja la barra de navegacion superior (igual en todas las
 * paginas), cambiando los links segun si hay sesion iniciada.
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