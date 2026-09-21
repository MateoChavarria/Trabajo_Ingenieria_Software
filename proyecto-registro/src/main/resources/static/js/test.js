// ============================================================
// ESTADO DEL TEST (todo lo que se necesita mientras el usuario responde)
// ============================================================
let preguntas = [];               // la lista completa que trae el backend
let indiceActual = 0;             // en que pregunta va el usuario ahora
const respuestasSeleccionadas = {}; // { idPregunta: idOpcion }, se va llenando en memoria

// Elementos del DOM que vamos a actualizar seguido, los guardamos una
// sola vez en variables para no tener que buscarlos cada vez.
const elementoPregunta = document.getElementById("pregunta");
const elementoOpciones = document.getElementById("opciones");
const elementoAviso = document.getElementById("avisoError");
const elementoBarraProgreso = document.getElementById("barraProgreso");
const elementoTextoProgreso = document.getElementById("textoProgreso");
const botonAnterior = document.getElementById("btnAnterior");
const botonSiguiente = document.getElementById("btnSiguiente");
const pantallaTest = document.getElementById("pantallaTest");
const pantallaResultado = document.getElementById("pantallaResultado");
const pantallaCarga = document.getElementById("pantallaCarga");

// ============================================================
// ARRANQUE: cargar el cuestionario apenas se abre la pagina
// ============================================================
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

// ============================================================
// MOSTRAR UNA PREGUNTA A LA VEZ
// ============================================================
function mostrarPregunta(indice) {
    const pregunta = preguntas[indice];

    elementoPregunta.textContent = `${indice + 1}/${preguntas.length} — ${pregunta.texto}`;
    elementoAviso.textContent = "";
    elementoOpciones.innerHTML = "";

    const opcionYaElegida = respuestasSeleccionadas[pregunta.id];

    pregunta.opciones.forEach((opcion) => {
        const etiqueta = document.createElement("label");
        etiqueta.className = "opcion";

        const radio = document.createElement("input");
        radio.type = "radio";
        radio.name = "opcion";
        radio.value = opcion.id;
        radio.checked = opcionYaElegida === opcion.id;

        etiqueta.appendChild(radio);
        etiqueta.append(" " + opcion.texto);
        elementoOpciones.appendChild(etiqueta);
    });

    botonAnterior.disabled = indice === 0;
    botonSiguiente.textContent = (indice === preguntas.length - 1) ? "Finalizar" : "Siguiente";
}

// ============================================================
// BARRA Y TEXTO DE PROGRESO
// Tarea tecnica: "calcular el porcentaje de avance segun cuantas
// preguntas van respondidas del total" + "actualizar ese indicador
// cada vez que el usuario avanza o retrocede una pregunta"
// ============================================================
function actualizarBarraProgreso() {
    const totalPreguntas = preguntas.length;
    const totalRespondidas = Object.keys(respuestasSeleccionadas).length;
    const porcentaje = totalPreguntas === 0 ? 0 : Math.round((totalRespondidas / totalPreguntas) * 100);

    elementoBarraProgreso.style.width = porcentaje + "%";
    elementoTextoProgreso.textContent = `${totalRespondidas} de ${totalPreguntas} preguntas respondidas`;
}

// ============================================================
// LEER LA OPCION QUE EL USUARIO MARCO EN PANTALLA
// ============================================================
function obtenerOpcionSeleccionada() {
    const radioMarcado = document.querySelector('input[name="opcion"]:checked');
    return radioMarcado ? parseInt(radioMarcado.value, 10) : null;
}

// ============================================================
// BOTON "ANTERIOR"
// ============================================================
botonAnterior.addEventListener("click", () => {
    if (indiceActual === 0) return;
    indiceActual--;
    mostrarPregunta(indiceActual);
    actualizarBarraProgreso();
});

// ============================================================
// BOTON "SIGUIENTE" / "FINALIZAR"
// ============================================================
botonSiguiente.addEventListener("click", async () => {
    const preguntaActual = preguntas[indiceActual];
    const idOpcionElegida = obtenerOpcionSeleccionada();

    // No dejar avanzar si el usuario no respondio la pregunta obligatoria.
    if (idOpcionElegida === null) {
        elementoAviso.textContent = "Debes seleccionar una opción para continuar.";
        return;
    }

    // Guardar temporalmente en pantalla la respuesta marcada.
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

// ============================================================
// ENVIAR TODAS LAS RESPUESTAS AL BACKEND Y MOSTRAR EL RESULTADO
// ============================================================
async function enviarRespuestasYMostrarResultado() {
    const sesionGuardada = localStorage.getItem("sesion");
    if (!sesionGuardada) {
        elementoAviso.textContent = "Tu sesión expiró. Vuelve a iniciar sesión.";
        return;
    }
    const sesion = JSON.parse(sesionGuardada);

    // Convertimos el objeto { idPregunta: idOpcion } a la lista que
    // espera el backend: [{ preguntaId, opcionId }, ...]
    const respuestas = Object.entries(respuestasSeleccionadas).map(([preguntaId, opcionId]) => ({
        preguntaId: parseInt(preguntaId, 10),
        opcionId: opcionId
    }));

    // Mostrar la pantalla de carga mientras se calcula el resultado.
    pantallaTest.style.display = "none";
    pantallaCarga.style.display = "flex";

    try {
        const respuesta = await fetch("/api/test/resultado", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                usuarioId: sesion.idUsuario,
                respuestas: respuestas
            })
        });

        if (respuesta.ok) {
            const resultado = await respuesta.json();
            mostrarResultado(resultado);
        } else {
            const textoError = await respuesta.text();
            pantallaCarga.style.display = "none";
            pantallaTest.style.display = "block";
            elementoAviso.textContent = "No se pudo calcular el resultado: " + textoError;
        }

    } catch (error) {
        pantallaCarga.style.display = "none";
        pantallaTest.style.display = "block";
        elementoAviso.textContent = "No se pudo conectar con el servidor.";
    }
}

// ============================================================
// MOSTRAR EL RESULTADO FINAL (puntaje por area)
// ============================================================
function mostrarResultado(resultado) {
    pantallaCarga.style.display = "none";
    pantallaResultado.style.display = "block";

    const lista = document.getElementById("listaResultado");
    lista.innerHTML = "";

    Object.entries(resultado).forEach(([area, puntaje]) => {
        const item = document.createElement("li");
        item.textContent = `${area}: ${puntaje} puntos`;
        lista.appendChild(item);
    });
}

// Arrancar todo al cargar la pagina.
cargarCuestionario();
