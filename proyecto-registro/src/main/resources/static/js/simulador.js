// Simulador de carrera: se recorre el plan de estudios semestre por
// semestre. Los datos vienen de GET /api/carreras/{id}/simulacion, que
// solo responde para carreras de la UPB Medellin. Todo el texto se
// inserta con textContent (via crearElemento), nunca con innerHTML.

const parametros = new URLSearchParams(window.location.search);
const idCarrera = parametros.get("id");
const idEsValido = /^[1-9][0-9]{0,8}$/.test(idCarrera || "");

const enlaceVolver = document.getElementById("linkVolver");
enlaceVolver.href = idEsValido ? enlaceDetalleCarrera(idCarrera) : "explorar.html";

const barraProgreso = new BarraProgreso(
    document.getElementById("barraProgreso"),
    document.getElementById("textoProgreso"),
    "Semestre {actual} de {total}"
);

let simulacion = null;   // datos completos recibidos del backend
let indiceActual = 0;    // posicion (desde 0) del semestre que se esta viendo

if (!idEsValido) {
    document.getElementById("nombreCarrera").textContent = "No se encontró la carrera.";
} else {
    cargarSimulacion();
}

async function cargarSimulacion() {
    try {
        const respuesta = await fetch(`/api/carreras/${encodeURIComponent(idCarrera)}/simulacion`);

        if (!respuesta.ok) {
            // El backend explica el motivo (por ejemplo, "solo disponible para carreras de la UPB Medellín").
            document.getElementById("nombreCarrera").textContent = "No se puede simular esta carrera.";
            mostrarAlerta(document.getElementById("mensajeError"), "aviso",
                (await respuesta.text()) || "No se pudo cargar la simulación.");
            return;
        }

        simulacion = await respuesta.json();
        iniciarSimulacion();

    } catch (error) {
        document.getElementById("nombreCarrera").textContent = "No se pudo conectar con el servidor.";
    }
}

function iniciarSimulacion() {
    document.getElementById("nombreCarrera").textContent = `Simulación: ${simulacion.nombreCarrera}`;
    document.title = `Simulación de ${simulacion.nombreCarrera} - Orientador Vocacional UPB`;
    document.getElementById("avisoReferencial").textContent = simulacion.aviso;
    document.getElementById("avisoResumen").textContent = simulacion.aviso;
    document.getElementById("simulacion").hidden = false;

    document.getElementById("btnAnterior").addEventListener("click", () => irAlSemestre(indiceActual - 1));
    document.getElementById("btnSiguiente").addEventListener("click", () => irAlSemestre(indiceActual + 1));

    mostrarSemestreActual();
}

/** Se mueve de a un semestre; los limites (primero y ultimo) los respeta aqui mismo. */
function irAlSemestre(indice) {
    if (indice < 0 || indice >= simulacion.semestres.length) {
        return;
    }
    indiceActual = indice;
    mostrarSemestreActual();
}

function mostrarSemestreActual() {
    const total = simulacion.semestres.length;
    const semestre = simulacion.semestres[indiceActual];

    barraProgreso.actualizar(indiceActual + 1, total);
    pintarLineaTiempo();
    pintarTarjetaSemestre(semestre);

    document.getElementById("btnAnterior").disabled = indiceActual === 0;
    document.getElementById("btnSiguiente").disabled = indiceActual === total - 1;

    // El resumen general aparece solo al llegar al ultimo semestre.
    const esElUltimo = indiceActual === total - 1;
    document.getElementById("resumenFinal").hidden = !esElUltimo;
    if (esElUltimo) {
        pintarResumenFinal();
    }
}

/** Linea de tiempo: semestres cursados, el actual y los que faltan. */
function pintarLineaTiempo() {
    const lista = document.getElementById("lineaTiempo");
    lista.replaceChildren();

    simulacion.semestres.forEach((semestre, indice) => {
        const estado = indice < indiceActual ? "hecho" : indice === indiceActual ? "actual" : "pendiente";
        const paso = crearElemento("li", "paso-tiempo " + estado);
        paso.append(crearElemento("span", "punto-tiempo", semestre.numero));
        paso.title = `Semestre ${semestre.numero}: ${semestre.enfoquePrincipal}`;
        if (estado === "actual") {
            paso.setAttribute("aria-current", "step");
        }
        lista.appendChild(paso);
    });
}

/** Enfoque del semestre, su descripcion y sus materias con los temas mas dificiles de cada una. */
function pintarTarjetaSemestre(semestre) {
    const tarjeta = document.getElementById("tarjetaSemestre");
    tarjeta.replaceChildren(
        crearElemento("span", "badge-area", `Enfoque: ${semestre.enfoquePrincipal}`),
        crearElemento("h2", null, `Semestre ${semestre.numero}`),
        crearElemento("p", "descripcion-semestre", semestre.descripcion),
        crearListaMaterias(semestre)
    );
}

/** Una tarjeta por materia: nombre, creditos, enfoque y la lista de temas que mas cuestan. */
function crearListaMaterias(semestre) {
    const lista = crearElemento("div", "lista-materias-sim");

    semestre.materias.forEach((materia) => {
        const tarjeta = crearElemento("article", "materia-sim");

        const encabezado = crearElemento("div", "materia-sim-encabezado");
        encabezado.append(
            crearElemento("h3", null, materia.nombre),
            crearElemento("span", "chip-creditos", textoCreditos(materia.creditos))
        );
        tarjeta.append(encabezado, crearElemento("p", "enfoque-materia", materia.enfoqueTematico));

        if (materia.electiva) {
            // Electiva u optativa: el estudiante escoge la materia, asi que no hay temas propios.
            tarjeta.appendChild(crearElemento(
                "p", "nota-electiva",
                "Materia de libre elección: sus temas más difíciles dependen de la materia que escojas."
            ));
        } else if (Array.isArray(materia.temasDificiles) && materia.temasDificiles.length > 0) {
            const temas = crearElemento("ul", "temas-dificiles");
            materia.temasDificiles.forEach((tema) => temas.appendChild(crearElemento("li", null, tema)));
            tarjeta.append(crearElemento("h4", null, "Temas más difíciles"), temas);
        }
        lista.appendChild(tarjeta);
    });

    lista.appendChild(crearElemento("p", "total-semestre", `Total del semestre: ${textoCreditos(semestre.creditosTotales)}`));
    return lista;
}

function textoDuracion(totalSemestres) {
    const anios = totalSemestres / 2;
    const textoAnios = anios.toLocaleString("es-CO");
    return `${totalSemestres} semestres (${textoAnios} ${anios === 1 ? "año" : "años"})`;
}

function pintarResumenFinal() {
    const datos = document.getElementById("datosResumen");
    datos.replaceChildren();
    [
        ["Duración total", textoDuracion(simulacion.totalSemestres)],
        ["Cantidad de materias", simulacion.totalMaterias],
        ["Créditos en total", simulacion.creditosTotales]
    ].forEach(([etiqueta, valor]) => {
        const bloque = crearElemento("div", "dato-resumen");
        bloque.append(crearElemento("dt", null, etiqueta), crearElemento("dd", null, valor));
        datos.appendChild(bloque);
    });
}
