// Sprint 10: primero se muestra el estado del acceso. Sin acceso completo no
// se muestra el perfil vocacional ni el historial (son parte de lo que se paga).
esperarAcceso().then((acceso) => {
    pintarEstadoAcceso(acceso);
    if (acceso === false) {
        document.getElementById("seccionesPerfil").hidden = true;
        return;
    }
    cargarPerfilEHistorial();
});

function pintarEstadoAcceso(acceso) {
    const tarjeta = document.getElementById("estadoAcceso");
    tarjeta.replaceChildren();

    if (acceso === true) {
        tarjeta.append(
            crearElemento("span", "badge-area", "Acceso completo"),
            crearElemento("p", "motivo", "Tienes desbloqueados el test vocacional, tus carreras recomendadas y el simulador de carreras.")
        );
    } else if (acceso === false) {
        const boton = crearElemento("a", "btn sun", "Desbloquear acceso completo");
        boton.href = "pago.html";
        boton.style.marginTop = "12px";
        tarjeta.append(
            crearElemento("span", "badge-bloqueado", "🔒 Acceso gratuito"),
            crearElemento("p", "motivo", "Puedes explorar las carreras, sus planes de estudio y las universidades. El test vocacional, tus carreras recomendadas y el simulador se desbloquean con el acceso completo."),
            boton
        );
    } else {
        tarjeta.appendChild(crearElemento("p", "motivo", "No se pudo verificar tu acceso en este momento."));
    }
}

/** Cuantos intentos se muestran antes del boton "Ver todos". */
const INTENTOS_VISIBLES = 4;

function formatoFecha(texto) {
    const fecha = new Date(texto);
    return Number.isNaN(fecha.getTime()) ? null : fecha;
}

function mostrar(id, visible) {
    const elemento = document.getElementById(id);
    if (elemento) elemento.hidden = !visible;
}

/**
 * El perfil se arma con el test MAS RECIENTE que guarda el servidor, asi se ve
 * igual en cualquier navegador. Si el servidor no responde, se usa la copia
 * guardada en este navegador (si existe).
 */
async function cargarPerfilEHistorial() {
    let historial = null;   // null = no se pudo consultar

    if (obtenerSesion()) {
        try {
            const respuesta = await fetchAutenticado("/api/perfil/historial");
            if (respuesta.ok) historial = await respuesta.json();
        } catch (error) {
            historial = null;
        }
    }

    if (Array.isArray(historial) && historial.length > 0) {
        renderizarPerfil(historial[0].perfil, formatoFecha(historial[0].fecha));
        dibujarHistorial(historial);
        return;
    }

    const copiaLocal = leerJsonLocal("perfilVocacional");
    if (historial === null && copiaLocal && Array.isArray(copiaLocal.areasDominantes)) {
        renderizarPerfil(copiaLocal, null);
        return;
    }

    // Sin tests (o sin poder consultarlos): se muestra un mensaje en vez de secciones vacias.
    mostrar("sinTests", true);
}

function renderizarPerfil(perfil, fecha) {
    const contenedorDominantes = document.getElementById("listaDominantes");
    contenedorDominantes.replaceChildren();

    perfil.areasDominantes.forEach((area, posicion) => {
        const tarjeta = crearElemento("div", "card hoverable");
        tarjeta.append(
            crearElemento("span", "badge-area", (posicion + 1) + ". " + area.nombreAmigable),
            crearBarraAfinidad("Afinidad", area.afinidad),
            crearElemento("p", "motivo", area.interpretacion)
        );
        contenedorDominantes.appendChild(tarjeta);
    });

    const textoFecha = document.getElementById("textoFechaPerfil");
    textoFecha.textContent = fecha
        ? "Según tu test más reciente, del " + fecha.toLocaleDateString("es-CO", { dateStyle: "long" }) + "."
        : "";

    const contenedorCompleto = document.getElementById("listaCompleta");
    contenedorCompleto.replaceChildren();
    Object.entries(perfil.afinidadPorArea || {})
        .sort((a, b) => b[1] - a[1])
        .forEach(([codigoArea, afinidad]) => {
            contenedorCompleto.appendChild(crearBarraAfinidad(nombreArea(codigoArea), afinidad));
        });

    // La afinidad completa sale SOLO del test mas reciente (no mezcla ni promedia los anteriores).
    document.getElementById("textoFechaAfinidad").textContent = fecha
        ? "Corresponde únicamente a tu test más reciente, del " + fecha.toLocaleDateString("es-CO", { dateStyle: "long" }) + ". No es un promedio de tus tests anteriores."
        : "Corresponde únicamente a tu último test. No es un promedio de tests anteriores.";

    mostrar("seccionDominantes", perfil.areasDominantes.length > 0);
    mostrar("seccionAfinidad", contenedorCompleto.childElementCount > 0);
}

// ---------- Historial ----------

function crearBloqueFecha(fecha) {
    const bloque = crearElemento("div", "intento-fecha");
    if (!fecha) {
        bloque.appendChild(crearElemento("span", "dia", "?"));
        return bloque;
    }
    bloque.append(
        crearElemento("span", "dia", String(fecha.getDate())),
        crearElemento("span", "mes", fecha.toLocaleDateString("es-CO", { month: "short" }).replace(".", "") + " " + fecha.getFullYear()),
        crearElemento("span", "hora", fecha.toLocaleTimeString("es-CO", { hour: "2-digit", minute: "2-digit" }))
    );
    return bloque;
}

function crearIntento(intento, esElMasReciente) {
    const fila = crearElemento("div", "intento" + (esElMasReciente ? " reciente" : ""));

    const areas = crearElemento("div", "intento-areas");
    if (esElMasReciente) {
        areas.appendChild(crearElemento("span", "etiqueta-reciente", "Más reciente"));
    }
    intento.perfil.areasDominantes.forEach((area, posicion) => {
        const chip = crearElemento("span", "chip-area" + (posicion === 0 ? " principal" : ""));
        chip.append(area.nombreAmigable + " ", crearElemento("b", null, porcentajeSeguro(area.afinidad) + "%"));
        areas.appendChild(chip);
    });

    if (esElMasReciente) {
        const verCarreras = crearElemento("a", "btn sun btn-mini", "Ver carreras recomendadas");
        verCarreras.href = "carreras.html";
        areas.appendChild(verCarreras);
    }

    fila.append(crearBloqueFecha(formatoFecha(intento.fecha)), areas);
    return fila;
}

function dibujarHistorial(historial) {
    const contenedor = document.getElementById("listaHistorial");
    contenedor.replaceChildren();

    historial.forEach((intento, posicion) => {
        const fila = crearIntento(intento, posicion === 0);
        if (posicion >= INTENTOS_VISIBLES) fila.hidden = true;
        contenedor.appendChild(fila);
    });

    if (historial.length > INTENTOS_VISIBLES) {
        const boton = crearElemento("button", "btn outline", "Ver todos (" + historial.length + ")");
        boton.type = "button";
        boton.addEventListener("click", () => {
            contenedor.querySelectorAll(".intento[hidden]").forEach((fila) => { fila.hidden = false; });
            boton.remove();
        });
        contenedor.appendChild(boton);
    }
    mostrar("seccionHistorial", true);
}
