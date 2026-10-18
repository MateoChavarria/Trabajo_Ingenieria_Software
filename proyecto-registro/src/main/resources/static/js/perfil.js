const datosGuardados = leerJsonLocal("perfilVocacional");

if (!datosGuardados || !Array.isArray(datosGuardados.areasDominantes)) {
    document.getElementById("listaDominantes").textContent =
        "No hay un perfil guardado. Vuelve a realizar el test.";
} else {
    renderizarPerfil(datosGuardados);
}

function renderizarPerfil(perfil) {
    const contenedorDominantes = document.getElementById("listaDominantes");
    contenedorDominantes.replaceChildren();

    perfil.areasDominantes.forEach((area) => {
        const tarjeta = crearElemento("div", "card hoverable");
        tarjeta.append(
            crearElemento("span", "badge-area", area.nombreAmigable),
            crearBarraAfinidad("Afinidad", area.afinidad),
            crearElemento("p", "motivo", area.interpretacion)
        );
        contenedorDominantes.appendChild(tarjeta);
    });

    const contenedorCompleto = document.getElementById("listaCompleta");
    contenedorCompleto.replaceChildren();

    const areasOrdenadas = Object.entries(perfil.afinidadPorArea || {}).sort((a, b) => b[1] - a[1]);

    areasOrdenadas.forEach(([codigoArea, afinidad]) => {
        contenedorCompleto.appendChild(crearBarraAfinidad(nombreArea(codigoArea), afinidad));
    });
}

async function cargarHistorial() {
    const contenedor = document.getElementById("listaHistorial");
    if (!obtenerSesion() || !contenedor) return;

    try {
        // El servidor identifica al usuario por su token; ya no se envia
        // el id en la URL.
        const respuesta = await fetchAutenticado("/api/perfil/historial");
        if (!respuesta.ok) {
            contenedor.textContent = "No se pudo cargar el historial.";
            return;
        }

        const historial = await respuesta.json();

        if (historial.length === 0) {
            contenedor.textContent = "Este es tu primer test.";
            return;
        }

        contenedor.replaceChildren();
        historial.forEach((intento) => {
            const fecha = new Date(intento.fecha).toLocaleString("es-CO", {
                dateStyle: "medium", timeStyle: "short"
            });
            const areas = intento.perfil.areasDominantes.map(a => a.nombreAmigable).join(", ");

            const fila = crearElemento("p");
            fila.style.marginBottom = "10px";
            fila.append(crearElemento("b", null, fecha), " — " + areas);
            contenedor.appendChild(fila);
        });

    } catch (error) {
        contenedor.textContent = "No se pudo conectar con el servidor.";
    }
}

cargarHistorial();