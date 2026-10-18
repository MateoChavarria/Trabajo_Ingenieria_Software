/**
 * Piezas de la vista del plan de estudios que se comparten entre
 * carrera-detalle.html y simulador.html: la tabla de materias de un
 * semestre y la ventana con el detalle de una materia.
 * Requiere que js/utilidadesDom.js se cargue antes.
 */

function crearTablaMaterias(semestre) {
    const tabla = crearElemento("table", "tabla-pensum");

    const encabezado = crearElemento("tr");
    encabezado.append(
        crearElemento("th", null, "Materia"),
        crearElemento("th", "num", "Créditos"),
        crearElemento("th", null, "Enfoque")
    );
    tabla.appendChild(crearElemento("thead")).appendChild(encabezado);

    const cuerpo = crearElemento("tbody");
    semestre.materias.forEach((materia) => {
        const fila = crearElemento("tr", "materia-fila");

        // El nombre va dentro de un botón: así se puede abrir también con el teclado.
        const botonNombre = crearElemento("button", "materia-boton", materia.nombre);
        botonNombre.type = "button";
        const celdaNombre = crearElemento("td");
        celdaNombre.appendChild(botonNombre);

        fila.append(
            celdaNombre,
            crearElemento("td", "num", materia.creditos),
            crearElemento("td", null, materia.enfoqueTematico)
        );
        fila.addEventListener("click", () => mostrarDetalleMateria(materia, semestre));
        cuerpo.appendChild(fila);
    });
    tabla.appendChild(cuerpo);

    const total = crearElemento("tr");
    total.append(
        crearElemento("td", null, "Total del semestre"),
        crearElemento("td", "num", semestre.creditosTotales),
        crearElemento("td")
    );
    tabla.appendChild(crearElemento("tfoot")).appendChild(total);

    return tabla;
}


/** Crea una sola vez la ventana emergente donde se muestra el detalle de una materia. */
function obtenerDialogoMateria() {
    let dialogo = document.getElementById("dialogoMateria");
    if (!dialogo) {
        dialogo = crearElemento("dialog", "dialogo-materia");
        dialogo.id = "dialogoMateria";
        // Un clic en el fondo oscuro (fuera de la tarjeta) cierra la ventana.
        dialogo.addEventListener("click", (evento) => {
            if (evento.target === dialogo) {
                dialogo.close();
            }
        });
        document.body.appendChild(dialogo);
    }
    return dialogo;
}

function textoCreditos(creditos) {
    return creditos === 1 ? "1 crédito" : `${creditos} créditos`;
}

/** Muestra el nombre, los créditos y el enfoque de la materia, y el semestre al que pertenece. */
function mostrarDetalleMateria(materia, semestre) {
    const dialogo = obtenerDialogoMateria();

    const datos = crearElemento("dl", "datos-materia");
    [
        ["Créditos", textoCreditos(materia.creditos)],
        ["Enfoque", materia.enfoqueTematico],
        ["Semestre", `Semestre ${semestre.numero}`]
    ].forEach(([etiqueta, valor]) => {
        datos.append(crearElemento("dt", null, etiqueta), crearElemento("dd", null, valor));
    });

    const botonCerrar = crearElemento("button", "btn outline", "Cerrar");
    botonCerrar.type = "button";
    botonCerrar.addEventListener("click", () => dialogo.close());

    const contenido = crearElemento("div", "contenido-dialogo");
    contenido.append(crearElemento("h3", null, materia.nombre), datos, botonCerrar);

    dialogo.replaceChildren(contenido);
    dialogo.showModal();
}
