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