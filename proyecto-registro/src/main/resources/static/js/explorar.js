async function cargarCarreras() {
    const contenedor = document.getElementById("listaCarreras");
    try {
        const respuesta = await fetch("/api/carreras");
        if (!respuesta.ok) {
            contenedor.textContent = "No se pudieron cargar las carreras.";
            return;
        }
        const carreras = await respuesta.json();

        contenedor.replaceChildren();
        carreras.forEach((carrera) => {
            const tarjeta = crearElemento("div", "card hoverable");

            const enlace = crearElemento("a", "btn outline", "Ver detalle");
            enlace.href = enlaceDetalleCarrera(carrera.id);
            enlace.style.marginTop = "12px";
            enlace.style.display = "inline-block";

            tarjeta.append(
                crearElemento("span", "badge-area", carrera.areaCategoria),
                crearElemento("h3", null, carrera.nombre),
                crearElemento("p", "motivo", carrera.campoLaboral),
                enlace
            );
            contenedor.appendChild(tarjeta);
        });

    } catch (error) {
        contenedor.textContent = "No se pudo conectar con el servidor.";
    }
}

cargarCarreras();