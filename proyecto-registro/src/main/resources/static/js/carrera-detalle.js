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
    document.getElementById("badgeArea").textContent = nombreArea(carrera.areaCategoria);
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

        // Se espera a saber si el usuario pago, para dibujar (o no) el candado del simulador.
        const accesoCompleto = (await esperarAcceso()) === true;
        mostrarPensum(contenedor, await respuesta.json(), accesoCompleto);

    } catch (error) {
        contenedor.textContent = "No se pudo conectar con el servidor.";
    }
}

function mostrarPensum(contenedor, pensum, accesoCompleto) {
    contenedor.replaceChildren();

    if (!Array.isArray(pensum.semestres) || pensum.semestres.length === 0) {
        contenedor.textContent = "Esta carrera aún no tiene plan de estudios cargado.";
        return;
    }

    contenedor.appendChild(crearElemento(
        "p", "pensum-resumen",
        `${pensum.semestres.length} semestres · ${pensum.creditosTotales} créditos en total`
    ));

    // Solo llegamos aqui si la carrera tiene pensum (es de la UPB Medellin), asi que se puede simular,
    // pero la simulacion es una funcion de acceso completo: sin pagar se muestra con candado.
    const botonSimular = crearElemento("a", "btn sun",
        accesoCompleto ? "Simular esta carrera" : "🔒 Simular esta carrera");
    if (accesoCompleto || !obtenerSesion()) {
        // Quien no ha iniciado sesion pasa primero por el login; el servidor decide el resto.
        botonSimular.href = enlaceSimulador(idCarrera);
    } else {
        botonSimular.href = "pago.html?volver=simulador.html&id=" + encodeURIComponent(idCarrera);
    }
    botonSimular.style.marginBottom = "14px";
    contenedor.appendChild(botonSimular);
    if (!accesoCompleto) {
        contenedor.appendChild(crearElemento("p", "motivo nota-bloqueo",
            "El simulador semestre por semestre se desbloquea con el acceso completo."));
    }

    pensum.semestres.forEach((semestre, indice) => {
        contenedor.appendChild(crearBloqueSemestre(semestre, indice === 0));
    });
}

function crearBloqueSemestre(semestre, abierto) {
    const bloque = crearElemento("details", "semestre-bloque");
    bloque.open = abierto;

    // El enfoque y la descripcion del semestre se muestran solo en la simulacion.
    bloque.append(
        crearElemento("summary", null, `Semestre ${semestre.numero}`),
        crearTablaMaterias(semestre)
    );
    return bloque;
}
