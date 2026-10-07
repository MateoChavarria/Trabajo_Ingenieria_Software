// Si llegamos aqui desde el listado de recomendaciones, usamos lo
// que ya quedo guardado (incluye afinidad y motivo). Si la pagina se
// abre directo (por ejemplo, un link compartido), consultamos al
// backend y mostramos solo los datos de la carrera, sin afinidad.

const parametros = new URLSearchParams(window.location.search);
const idCarrera = parametros.get("id");

const datosSeleccionados = localStorage.getItem("carreraSeleccionada");

if (datosSeleccionados) {
    const item = JSON.parse(datosSeleccionados);

    if (String(item.carrera.id) === idCarrera) {
        mostrarConAfinidad(item);
    } else {
        cargarSoloDetalle();
    }
} else {
    cargarSoloDetalle();
}

function mostrarConAfinidad(item) {
    mostrarDatosBase(item.carrera);

    document.getElementById("bloqueAfinidad").style.display = "block";
    document.getElementById("textoAfinidad").textContent = item.afinidad + "%";
    document.getElementById("barraAfinidad").style.width = item.afinidad + "%";
    document.getElementById("motivoRecomendacion").textContent = item.motivo;
}

async function cargarSoloDetalle() {
    try {
        const respuesta = await fetch(`/api/carreras/${idCarrera}`);

        if (!respuesta.ok) {
            document.getElementById("nombreCarrera").textContent = "No se encontró la carrera.";
            return;
        }

        mostrarDatosBase(await respuesta.json());

    } catch (error) {
        document.getElementById("nombreCarrera").textContent = "No se pudo conectar con el servidor.";
    }
}

function mostrarDatosBase(carrera) {
    document.getElementById("badgeArea").textContent = carrera.areaCategoria;
    document.getElementById("nombreCarrera").textContent = carrera.nombre;
    document.getElementById("descripcionCarrera").textContent = carrera.descripcion;
    document.getElementById("campoLaboralCarrera").textContent = carrera.campoLaboral;

    const duracion = carrera.duracionSemestres ? `${carrera.duracionSemestres} semestres` : "No especificada";
    const tarifa = carrera.tarifaSemestre
        ? `$${Number(carrera.tarifaSemestre).toLocaleString("es-CO")} por semestre`
        : "No especificada";

    document.getElementById("datosExtra").textContent = `Duración: ${duracion} · Tarifa: ${tarifa}`;
}