const datosGuardados = localStorage.getItem("carrerasRecomendadas");

if (!datosGuardados) {
    document.getElementById("listaCarreras").textContent =
        "No hay recomendaciones guardadas. Vuelve a realizar el test.";
} else {
    renderizarCarreras(JSON.parse(datosGuardados));
}

function renderizarCarreras(resultado) {
    if (resultado.afinidadBaja) {
        document.getElementById("avisoAfinidadBaja").style.display = "block";
    }

    const contenedor = document.getElementById("listaCarreras");
    contenedor.innerHTML = "";

    resultado.carreras.forEach((item) => {
        const tarjeta = document.createElement("div");
        tarjeta.className = "card hoverable";

        tarjeta.innerHTML = `
            <span class="badge-area">${item.carrera.areaCategoria}</span>
            <h3>${item.carrera.nombre}</h3>
            <div class="bar">
                <div><b>Afinidad</b><b>${item.afinidad}%</b></div>
                <span><i style="width:${item.afinidad}%"></i></span>
            </div>
            <p class="motivo">${item.motivo}</p>
            <a class="btn" href="carrera-detalle.html?id=${item.carrera.id}" style="margin-top:10px;display:inline-block">Ver detalle</a>
        `;

        tarjeta.querySelector("a").addEventListener("click", () => {
            localStorage.setItem("carreraSeleccionada", JSON.stringify(item));
        });

        contenedor.appendChild(tarjeta);
    });
}