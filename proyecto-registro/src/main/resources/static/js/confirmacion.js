// Pantalla de confirmacion: muestra si el pago fue exitoso, fallido o
// cancelado, con los datos del comprobante. Los datos vienen de
// GET /api/pagos/{referencia}. Todo el texto se inserta con textContent.

const parametrosConfirmacion = new URLSearchParams(window.location.search);
const referenciaConfirmada = parametrosConfirmacion.get("ref");
const contenedorResultado = document.getElementById("resultadoPago");

if (!/^PAY-[0-9A-F]{12}$/.test(referenciaConfirmada || "")) {
    contenedorResultado.replaceChildren();
    mostrarAlerta(contenedorResultado, "error", "No se encontró el pago.");
} else {
    cargarResultado();
}

async function cargarResultado() {
    try {
        const respuesta = await fetchAutenticado("/api/pagos/" + encodeURIComponent(referenciaConfirmada));
        if (!respuesta.ok) {
            mostrarAlerta(contenedorResultado, "error", (await respuesta.text()) || "No se pudo cargar el resultado del pago.");
            return;
        }
        await dibujarResultado(await respuesta.json());

    } catch (error) {
        mostrarAlerta(contenedorResultado, "error", "No se pudo conectar con el servidor.");
    }
}

function crearBoton(texto, destino, clase) {
    const enlace = crearElemento("a", "btn " + clase, texto);
    enlace.href = destino;
    return enlace;
}

function crearComprobante(pago) {
    const datos = crearElemento("dl", "datos-pago");
    const fecha = new Date(pago.fechaResolucion || pago.fecha)
        .toLocaleString("es-CO", { dateStyle: "medium", timeStyle: "short" });

    [
        ["Referencia", pago.referencia],
        ["Monto", formatoPesos(pago.monto) + " " + pago.moneda],
        ["Estado", pago.estado.charAt(0).toUpperCase() + pago.estado.slice(1)],
        ["Fecha", fecha]
    ].forEach(([etiqueta, valor]) => {
        datos.append(crearElemento("dt", null, etiqueta), crearElemento("dd", null, valor));
    });
    return datos;
}

async function dibujarResultado(pago) {
    if (pago.estado === "pendiente") {
        // Todavia no termino: se vuelve a la pasarela.
        window.location.href = "pasarela.html?ref=" + encodeURIComponent(pago.referencia) + sufijoVolver();
        return;
    }

    contenedorResultado.replaceChildren();
    const tarjeta = crearElemento("div", "card");
    const acciones = crearElemento("div", "acciones-pago");

    if (pago.estado === "exitoso") {
        await consultarAcceso();   // actualiza los candados guardados
        pintarNav();

        tarjeta.append(
            crearElemento("h1", null, "¡Pago exitoso!"),
            crearElemento("div", "alerta exito", "Ya tienes acceso completo: test vocacional, carreras recomendadas y simulador."),
            crearComprobante(pago)
        );
        acciones.append(
            crearBoton("Ir al inicio", destinoSeguroDespuesDePago(), "sun"),
            crearBoton("Ver mi perfil", "perfil.html", "outline")
        );

    } else if (pago.estado === "fallido") {
        tarjeta.append(
            crearElemento("h1", null, "No se pudo completar el pago"),
            crearElemento("div", "alerta error", pago.motivoFallo || "La pasarela rechazó el pago."),
            crearElemento("p", "motivo", "No se te cobró nada. Puedes intentarlo de nuevo con otra tarjeta."),
            crearComprobante(pago)
        );
        acciones.append(
            crearBoton("Intentar de nuevo", enlaceConVolver("pago.html"), "sun"),
            crearBoton("Seguir explorando", "explorar.html", "outline")
        );

    } else {
        tarjeta.append(
            crearElemento("h1", null, "Pago cancelado"),
            crearElemento("div", "alerta aviso", "Cancelaste el pago. No se cobró nada."),
            crearComprobante(pago)
        );
        acciones.append(
            crearBoton("Volver a intentarlo", enlaceConVolver("pago.html"), "sun"),
            crearBoton("Seguir explorando", "explorar.html", "outline")
        );
    }

    tarjeta.appendChild(acciones);
    contenedorResultado.appendChild(tarjeta);
}
