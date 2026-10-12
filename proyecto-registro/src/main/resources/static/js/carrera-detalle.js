// Si llegamos aqui desde el listado de recomendaciones, usamos lo
// que ya quedo guardado (incluye afinidad y motivo). Si la pagina se
// abre directo (por ejemplo, un link compartido), consultamos al
// backend y mostramos solo los datos de la carrera, sin afinidad.

document.getElementById("linkVolver").addEventListener("click", (evento) => {
    evento.preventDefault();
    history.back();
});

const parametros = new URLSearchParams(window.location.search);
const idCarrera = parametros.get("id");
const idEsValido = /^[1-9][0-9]{0,8}$/.test(idCarrera || "");

const itemSeleccionado = leerJsonLocal("carreraSeleccionada");

if (!idEsValido) {
    document.getElementById("nombreCarrera").textContent = "No se encontró la carrera.";
} else if (itemSeleccionado && itemSeleccionado.carrera && String(itemSeleccionado.carrera.id) === idCarrera) {
    mostrarConAfinidad(itemSeleccionado);
} else {
    cargarSoloDetalle();
}

// El plan de estudios se pide aparte: solo existe para carreras de la UPB Medellín.
if (idEsValido) {
    cargarPensum();
}

function mostrarConAfinidad(item) {
    mostrarDatosBase(item.carrera);

    const afinidad = porcentajeSeguro(item.afinidad);
    document.getElementById("bloqueAfinidad").style.display = "block";
    document.getElementById("textoAfinidad").textContent = afinidad + "%";
    document.getElementById("barraAfinidad").style.width = afinidad + "%";
    document.getElementById("motivoRecomendacion").textContent = item.motivo;
}

async function cargarSoloDetalle() {
    try {
        const respuesta = await fetch(`/api/carreras/${encodeURIComponent(idCarrera)}`);

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

async function cargarPensum() {
    const contenedor = document.getElementById("contenidoPensum");
    try {
        const respuesta = await fetch(`/api/carreras/${encodeURIComponent(idCarrera)}/pensum`);

        if (!respuesta.ok) {
            // El backend explica el motivo (por ejemplo, "solo disponible para carreras de la UPB Medellín").
            const mensaje = await respuesta.text();
            contenedor.textContent = mensaje || "No se pudo cargar el plan de estudios.";
            return;
        }

        mostrarPensum(contenedor, await respuesta.json());

    } catch (error) {
        contenedor.textContent = "No se pudo conectar con el servidor.";
    }
}

function mostrarPensum(contenedor, pensum) {
    contenedor.replaceChildren();

    if (!Array.isArray(pensum.semestres) || pensum.semestres.length === 0) {
        contenedor.textContent = "Esta carrera aún no tiene plan de estudios cargado.";
        return;
    }

    contenedor.appendChild(crearElemento(
        "p", "pensum-resumen",
        `${pensum.semestres.length} semestres · ${pensum.creditosTotales} créditos en total`
    ));

    pensum.semestres.forEach((semestre, indice) => {
        contenedor.appendChild(crearBloqueSemestre(semestre, indice === 0));
    });
}

function crearBloqueSemestre(semestre, abierto) {
    const bloque = crearElemento("details", "semestre-bloque");
    bloque.open = abierto;

    bloque.append(
        crearElemento("summary", null, `Semestre ${semestre.numero} · ${semestre.enfoquePrincipal}`),
        crearElemento("p", "descripcion-semestre", semestre.descripcion),
        crearTablaMaterias(semestre)
    );
    return bloque;
}

function crearTablaMaterias(semestre) {
    const tabla = crearElemento("table", "tabla-pensum");

    const encabezado = crearElemento("tr");
    encabezado.append(
        crearElemento("th", null, "Materia"),
        crearElemento("th", "num", "Créditos"),
        crearElemento("th", null, "Enfoque")
    );
    tabla.appendChild(crearElemento("thead")).appendChild(encabezado);

    const cuerpo = crearElemento("tbody");
    semestre.materias.forEach((materia) => {
        const fila = crearElemento("tr");
        fila.append(
            crearElemento("td", null, materia.nombre),
            crearElemento("td", "num", materia.creditos),
            crearElemento("td", null, materia.enfoqueTematico)
        );
        cuerpo.appendChild(fila);
    });
    tabla.appendChild(cuerpo);

    const total = crearElemento("tr");
    total.append(
        crearElemento("td", null, "Total del semestre"),
        crearElemento("td", "num", semestre.creditosTotales),
        crearElemento("td")
    );
    tabla.appendChild(crearElemento("tfoot")).appendChild(total);

    return tabla;
}   