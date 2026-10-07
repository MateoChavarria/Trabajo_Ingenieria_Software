async function cargarCarreras() {
    const contenedor = document.getElementById("listaCarreras");
    try {
        const respuesta = await fetch("/api/carreras");
        if (!respuesta.ok) {
            contenedor.textContent = "No se pudieron cargar las carreras.";
            return;
        }
        const carreras = await respuesta.json();

        contenedor.innerHTML = "";
        carreras.forEach((carrera) => {
            const tarjeta = document.createElement("div");
            tarjeta.className = "card hoverable";
            tarjeta.innerHTML = `
                <span class="badge-area">${carrera.areaCategoria}</span>
                <h3>${carrera.nombre}</h3>
                <p class="motivo">${carrera.campoLaboral}</p>
                <a class="btn outline" href="carrera-detalle.html?id=${carrera.id}" style="margin-top:12px;display:inline-block">Ver detalle</a>
            `;
            contenedor.appendChild(tarjeta);
        });

    } catch (error) {
        contenedor.textContent = "No se pudo conectar con el servidor.";
    }
}

cargarCarreras();