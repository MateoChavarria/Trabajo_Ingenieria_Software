const datosGuardados = leerJsonLocal("carrerasRecomendadas");

if (!datosGuardados || !Array.isArray(datosGuardados.carreras)) {
    document.getElementById("listaCarreras").textContent =
        "No hay recomendaciones guardadas. Vuelve a realizar el test.";
} else {
    renderizarCarreras(datosGuardados);
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