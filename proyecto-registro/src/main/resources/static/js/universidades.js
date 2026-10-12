/**
 * Pantalla "Universidades": muestra tarjetas con todas las
 * universidades y un buscador simple por nombre.
 * Requiere que js/utilidadesDom.js se cargue antes.
 */

let todasLasUniversidades = [];

/** Pasa a minúsculas y quita tildes, para que "antioquia" encuentre "Antioquía". */
function normalizarTexto(texto) {
    return String(texto || "")
        .normalize("NFD")
        .replace(/[̀-ͯ]/g, "")
        .toLowerCase()
        .trim();
}

async function cargarUniversidades() {
    const contenedor = document.getElementById("listaUniversidades");
    try {
        const respuesta = await fetch("/api/universidades");
        if (!respuesta.ok) {
            contenedor.textContent = "No se pudieron cargar las universidades.";
            return;
        }
        todasLasUniversidades = await respuesta.json();
        mostrarUniversidades(todasLasUniversidades);

    } catch (error) {
        contenedor.textContent = "No se pudo conectar con el servidor.";
    }
}

function mostrarUniversidades(universidades) {
    const contenedor = document.getElementById("listaUniversidades");
    contenedor.replaceChildren();

    document.getElementById("contadorUniversidades").textContent =
        universidades.length === 1 ? "1 universidad" : universidades.length + " universidades";

    if (universidades.length === 0) {
        contenedor.textContent = "No hay universidades que coincidan con tu búsqueda.";
        return;
    }

    universidades.forEach((universidad) => {
        const tarjeta = crearElemento("div", "card hoverable");

        const etiquetaInformacion = universidad.tieneInformacionCompleta
            ? crearElemento("span", "badge-completa", "Información completa")
            : crearElemento("span", "badge-parcial", "Información no disponible aún");

        tarjeta.append(
            crearElemento("span", "badge-area", universidad.caracter),
            crearElemento("h3", null, universidad.nombre),
            crearElemento("p", "motivo", "Sede(s): " + universidad.municipio),
            etiquetaInformacion
        );

        if (universidad.tieneInformacionCompleta) {
            const enlace = crearElemento("a", "btn outline", "Ver carreras");
            enlace.href = "explorar.html";
            enlace.style.marginTop = "12px";
            enlace.style.display = "inline-block";
            tarjeta.appendChild(enlace);
        }

        contenedor.appendChild(tarjeta);
    });
}

function filtrarUniversidades() {
    const texto = normalizarTexto(document.getElementById("buscadorUniversidades").value);
    const filtradas = todasLasUniversidades.filter(
        (universidad) => normalizarTexto(universidad.nombre).includes(texto)
    );
    mostrarUniversidades(filtradas);
}

document.getElementById("buscadorUniversidades").addEventListener("input", filtrarUniversidades);
cargarUniversidades();
