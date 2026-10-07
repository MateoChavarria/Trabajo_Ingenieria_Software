const datosGuardados = localStorage.getItem("perfilVocacional");

if (!datosGuardados) {
    document.getElementById("listaDominantes").textContent =
        "No hay un perfil guardado. Vuelve a realizar el test.";
} else {
    renderizarPerfil(JSON.parse(datosGuardados));
}

function renderizarPerfil(perfil) {
    const contenedorDominantes = document.getElementById("listaDominantes");
    contenedorDominantes.innerHTML = "";

    perfil.areasDominantes.forEach((area) => {
        const tarjeta = document.createElement("div");
        tarjeta.className = "card hoverable";
        tarjeta.innerHTML = `
            <span class="badge-area">${area.nombreAmigable}</span>
            <div class="bar">
                <div><b>Afinidad</b><b>${area.afinidad}%</b></div>
                <span><i style="width:${area.afinidad}%"></i></span>
            </div>
            <p class="motivo">${area.interpretacion}</p>
        `;
        contenedorDominantes.appendChild(tarjeta);
    });

    const contenedorCompleto = document.getElementById("listaCompleta");
    contenedorCompleto.innerHTML = "";

    const areasOrdenadas = Object.entries(perfil.afinidadPorArea).sort((a, b) => b[1] - a[1]);

    areasOrdenadas.forEach(([codigoArea, afinidad]) => {
        const bloque = document.createElement("div");
        bloque.className = "bar";
        bloque.innerHTML = `
            <div><b>${codigoArea}</b><b>${afinidad}%</b></div>
            <span><i style="width:${afinidad}%"></i></span>
        `;
        contenedorCompleto.appendChild(bloque);
    });
}

async function cargarHistorial() {
    const sesion = obtenerSesion();
    const contenedor = document.getElementById("listaHistorial");
    if (!sesion || !contenedor) return;

    try {
        const respuesta = await fetch(`/api/perfil/historial?usuarioId=${sesion.idUsuario}`);
        if (!respuesta.ok) {
            contenedor.textContent = "No se pudo cargar el historial.";
            return;
        }

        const historial = await respuesta.json();

        if (historial.length === 0) {
            contenedor.textContent = "Este es tu primer test.";
            return;
        }

        contenedor.innerHTML = "";
        historial.forEach((intento) => {
            const fecha = new Date(intento.fecha).toLocaleString("es-CO", {
                dateStyle: "medium", timeStyle: "short"
            });
            const areas = intento.perfil.areasDominantes.map(a => a.nombreAmigable).join(", ");

            const fila = document.createElement("p");
            fila.style.marginBottom = "10px";
            fila.innerHTML = `<b>${fecha}</b> — ${areas}`;
            contenedor.appendChild(fila);
        });

    } catch (error) {
        contenedor.textContent = "No se pudo conectar con el servidor.";
    }
}

cargarHistorial();