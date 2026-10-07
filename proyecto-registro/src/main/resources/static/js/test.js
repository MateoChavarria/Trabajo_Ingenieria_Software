// ============================================================
// ESTADO DEL TEST
// ============================================================
let preguntas = [];
let indiceActual = 0;
const respuestasSeleccionadas = {};

const elementoPregunta = document.getElementById("pregunta");
const elementoOpciones = document.getElementById("opciones");
const elementoAviso = document.getElementById("avisoError");
const elementoBarraProgreso = document.getElementById("barraProgreso");
const elementoTextoProgreso = document.getElementById("textoProgreso");
const botonAnterior = document.getElementById("btnAnterior");
const botonSiguiente = document.getElementById("btnSiguiente");
const pantallaTest = document.getElementById("pantallaTest");
const pantallaCarga = document.getElementById("pantallaCarga");

const barraProgreso = new BarraProgreso(elementoBarraProgreso, elementoTextoProgreso, "{actual} de {total} preguntas respondidas");

async function cargarCuestionario() {
    try {
        const respuesta = await fetch("/api/test/preguntas");

        if (!respuesta.ok) {
            elementoPregunta.textContent = "No se pudo cargar el test. Intenta de nuevo más tarde.";
            botonSiguiente.disabled = true;
            return;
        }

        preguntas = await respuesta.json();

        if (preguntas.length === 0) {
            elementoPregunta.textContent = "Todavía no hay preguntas configuradas.";
            botonSiguiente.disabled = true;
            return;
        }

        mostrarPregunta(indiceActual);
        actualizarBarraProgreso();

    } catch (error) {
        elementoPregunta.textContent = "No se pudo conectar con el servidor.";
    }
}

function mostrarPregunta(indice) {
    const pregunta = preguntas[indice];

    elementoPregunta.textContent = `${indice + 1}/${preguntas.length} — ${pregunta.texto}`;
    elementoAviso.innerHTML = "";
    elementoOpciones.innerHTML = "";

    const opcionYaElegida = respuestasSeleccionadas[pregunta.id];

    pregunta.opciones.forEach((opcion) => {
        const etiqueta = document.createElement("label");
        etiqueta.className = "opcion-test" + (opcionYaElegida === opcion.id ? " seleccionada" : "");

        const radio = document.createElement("input");
        radio.type = "radio";
        radio.name = "opcion";
        radio.value = opcion.id;
        radio.checked = opcionYaElegida === opcion.id;
        radio.addEventListener("change", () => {
            document.querySelectorAll(".opcion-test").forEach(el => el.classList.remove("seleccionada"));
            etiqueta.classList.add("seleccionada");
        });

        etiqueta.appendChild(radio);
        etiqueta.append(" " + opcion.texto);
        elementoOpciones.appendChild(etiqueta);
    });

    botonAnterior.disabled = indice === 0;
    botonSiguiente.textContent = (indice === preguntas.length - 1) ? "Finalizar" : "Siguiente";
}

function actualizarBarraProgreso() {
    const totalRespondidas = Object.keys(respuestasSeleccionadas).length;
    barraProgreso.actualizar(totalRespondidas, preguntas.length);
}

function obtenerOpcionSeleccionada() {
    const radioMarcado = document.querySelector('input[name="opcion"]:checked');
    return radioMarcado ? parseInt(radioMarcado.value, 10) : null;
}

botonAnterior.addEventListener("click", () => {
    if (indiceActual === 0) return;
    indiceActual--;
    mostrarPregunta(indiceActual);
    actualizarBarraProgreso();
});

botonSiguiente.addEventListener("click", async () => {
    const preguntaActual = preguntas[indiceActual];
    const idOpcionElegida = obtenerOpcionSeleccionada();

    if (idOpcionElegida === null) {
        elementoAviso.innerHTML = `<div class="alerta aviso">Debes seleccionar una opción para continuar.</div>`;
        return;
    }

    respuestasSeleccionadas[preguntaActual.id] = idOpcionElegida;
    actualizarBarraProgreso();

    const esLaUltimaPregunta = indiceActual === preguntas.length - 1;

    if (esLaUltimaPregunta) {
        await enviarRespuestasYMostrarResultado();
    } else {
        indiceActual++;
        mostrarPregunta(indiceActual);
    }
});

async function enviarRespuestasYMostrarResultado() {
    const sesion = obtenerSesion();
    if (!sesion) {
        elementoAviso.innerHTML = `<div class="alerta error">Tu sesión expiró. Vuelve a iniciar sesión.</div>`;
        return;
    }

    const respuestas = Object.entries(respuestasSeleccionadas).map(([preguntaId, opcionId]) => ({
        preguntaId: parseInt(preguntaId, 10),
        opcionId: opcionId
    }));

    pantallaTest.style.display = "none";
    pantallaCarga.style.display = "flex";

    try {
        const respuestaTest = await fetch("/api/test/resultado", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ usuarioId: sesion.idUsuario, respuestas: respuestas })
        });

        if (!respuestaTest.ok) {
            const textoError = await respuestaTest.text();
            pantallaCarga.style.display = "none";
            pantallaTest.style.display = "block";
            elementoAviso.innerHTML = `<div class="alerta error">No se pudo calcular el resultado: ${textoError}</div>`;
            return;
        }

        const afinidadPorArea = await respuestaTest.json();

        const respuestaCarreras = await fetch("/api/carreras/recomendadas", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ afinidadPorArea: afinidadPorArea })
        });

        if (!respuestaCarreras.ok) {
            const textoError = await respuestaCarreras.text();
            pantallaCarga.style.display = "none";
            pantallaTest.style.display = "block";
            elementoAviso.innerHTML = `<div class="alerta error">No se pudieron obtener las carreras recomendadas: ${textoError}</div>`;
            return;
        }

        const recomendaciones = await respuestaCarreras.json();
        localStorage.setItem("carrerasRecomendadas", JSON.stringify(recomendaciones));
        localStorage.setItem("afinidadPorArea", JSON.stringify(afinidadPorArea));

        const respuestaPerfil = await fetch("/api/perfil/resumen", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ afinidadPorArea: afinidadPorArea })
        });

        if (!respuestaPerfil.ok) {
            const textoError = await respuestaPerfil.text();
            pantallaCarga.style.display = "none";
            pantallaTest.style.display = "block";
            elementoAviso.innerHTML = `<div class="alerta error">No se pudo calcular tu perfil: ${textoError}</div>`;
            return;
        }

        const perfil = await respuestaPerfil.json();
        localStorage.setItem("perfilVocacional", JSON.stringify(perfil));

        window.location.href = "perfil.html";

    } catch (error) {
        pantallaCarga.style.display = "none";
        pantallaTest.style.display = "block";
        elementoAviso.innerHTML = `<div class="alerta error">No se pudo conectar con el servidor.</div>`;
    }
}

cargarCuestionario();   