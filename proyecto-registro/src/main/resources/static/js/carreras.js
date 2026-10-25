// Las recomendaciones se calculan con el test MAS RECIENTE que guarda el servidor,
// asi funcionan en cualquier navegador. Si el servidor no responde, se usa la
// copia guardada en este navegador (si existe).
cargarRecomendaciones();

async function cargarRecomendaciones() {
    const contenedor = document.getElementById("listaCarreras");

    try {
        const respuestaHistorial = await fetchAutenticado("/api/perfil/historial");
        if (respuestaHistorial.ok) {
            const historial = await respuestaHistorial.json();

            if (Array.isArray(historial) && historial.length > 0) {
                const ultimo = historial[0];
                const respuesta = await fetchAutenticado("/api/carreras/recomendadas", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ afinidadPorArea: ultimo.perfil.afinidadPorArea })
                });
                if (respuesta.ok) {
                    const fecha = new Date(ultimo.fecha);
                    if (!Number.isNaN(fecha.getTime())) {
                        document.getElementById("textoFechaCarreras").textContent =
                            "Según tu test más reciente, del " + fecha.toLocaleDateString("es-CO", { dateStyle: "long" }) + ".";
                    }
                    renderizarCarreras(await respuesta.json());
                    return;
                }
            } else {
                contenedor.textContent = "Aún no has hecho el test. Cuando lo termines, aquí verás tus carreras recomendadas.";
                return;
            }
        }
    } catch (error) {
        // Se intenta con la copia local.
    }

    const datosGuardados = leerJsonLocal("carrerasRecomendadas");
    if (datosGuardados && Array.isArray(datosGuardados.carreras)) {
        renderizarCarreras(datosGuardados);
    } else {
        contenedor.textContent = "No se pudieron cargar tus recomendaciones. Intenta de nuevo en un momento.";
    }
}

function renderizarCarreras(resultado) {
    if (resultado.afinidadBaja) {
        document.getElementById("avisoAfinidadBaja").style.display = "block";
    }

    const contenedor = document.getElementById("listaCarreras");
    contenedor.replaceChildren();

    resultado.carreras.forEach((item) => {
        const tarjeta = crearElemento("div", "card hoverable");

        const enlace = crearElemento("a", "btn", "Ver detalle");
        enlace.href = enlaceDetalleCarrera(item.carrera.id);
        enlace.style.marginTop = "10px";
        enlace.style.display = "inline-block";
        enlace.addEventListener("click", () => {
            localStorage.setItem("carreraSeleccionada", JSON.stringify(item));
        });

        tarjeta.append(
            crearElemento("span", "badge-area", nombreArea(item.carrera.areaCategoria)),
            crearElemento("h3", null, item.carrera.nombre),
            crearBarraAfinidad("Afinidad", item.afinidad),
            crearElemento("p", "motivo", item.motivo),
            enlace
        );

        contenedor.appendChild(tarjeta);
    });
}