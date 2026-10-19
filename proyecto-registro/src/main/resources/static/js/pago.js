// Pantalla de pago: muestra el resumen de lo que se desbloquea y el precio,
// e inicia el pago. Los datos vienen de GET /api/pagos/oferta. Todo el texto
// se inserta con textContent (via crearElemento), nunca con innerHTML.

const contenedorPago = document.getElementById("contenidoPago");
const mensajePago = document.getElementById("mensajePago");

cargarOferta();

async function cargarOferta() {
    try {
        const respuesta = await fetchAutenticado("/api/pagos/oferta");
        if (!respuesta.ok) {
            contenedorPago.replaceChildren();
            mostrarAlerta(mensajePago, "error", (await respuesta.text()) || "No se pudo cargar la información del pago.");
            return;
        }
        dibujarOferta(await respuesta.json());

    } catch (error) {
        contenedorPago.replaceChildren();
        mostrarAlerta(mensajePago, "error", "No se pudo conectar con el servidor.");
    }
}

function crearLista(items, clase) {
    const lista = crearElemento("ul", "lista-incluye " + clase);
    items.forEach((texto) => lista.appendChild(crearElemento("li", null, texto)));
    return lista;
}

function dibujarOferta(oferta) {
    contenedorPago.replaceChildren();

    const tarjetaDesbloquea = crearElemento("div", "card");
    tarjetaDesbloquea.append(
        crearElemento("h3", null, "Se desbloquea con el acceso completo"),
        crearLista(oferta.funcionesDesbloqueadas, "con-candado")
    );

    const tarjetaGratis = crearElemento("div", "card");
    tarjetaGratis.append(
        crearElemento("h3", null, "Siempre gratis"),
        crearLista(oferta.funcionesGratuitas, "gratis")
    );

    contenedorPago.append(tarjetaDesbloquea, tarjetaGratis);

    if (oferta.accesoCompleto) {
        guardarAcceso(true);
        dibujarYaTieneAcceso();
        return;
    }
    dibujarResumenDePago(oferta);
}

function dibujarYaTieneAcceso() {
    const tarjeta = crearElemento("div", "card");
    const enlace = crearElemento("a", "btn sun", "Continuar");
    const parametros = new URLSearchParams(window.location.search);
    enlace.href = destinoSeguroDespuesDePago(parametros.get("volver"), parametros.get("id"));

    tarjeta.append(
        crearElemento("div", "alerta exito", "Ya tienes acceso completo. No necesitas pagar de nuevo."),
        enlace
    );
    contenedorPago.appendChild(tarjeta);
}

function dibujarResumenDePago(oferta) {
    const tarjeta = crearElemento("div", "card");

    const precio = crearElemento("div", "precio-pago");
    precio.append(
        crearElemento("strong", null, formatoPesos(oferta.monto)),
        crearElemento("span", null, oferta.moneda + " · pago único")
    );

    const botonPagar = crearElemento("button", "btn sun block", "Pagar " + formatoPesos(oferta.monto));
    botonPagar.type = "button";
    botonPagar.addEventListener("click", () => iniciarPago(botonPagar));

    tarjeta.append(
        crearElemento("h3", null, "Resumen del pago"),
        precio,
        crearElemento("div", "alerta aviso",
            "Modo de pruebas: el pago es simulado y no se cobra dinero real."),
        botonPagar
    );
    contenedorPago.appendChild(tarjeta);
}

async function iniciarPago(boton) {
    mensajePago.replaceChildren();
    boton.disabled = true;

    try {
        const respuesta = await fetchAutenticado("/api/pagos/iniciar", { method: "POST" });

        if (respuesta.ok) {
            const pago = await respuesta.json();
            window.location.href = "pasarela.html?ref=" + encodeURIComponent(pago.referencia) + sufijoVolver();
            return;
        }

        mostrarAlerta(mensajePago, "error", (await respuesta.text()) || "No se pudo iniciar el pago.");
        if (respuesta.status === 409) {
            await cargarOferta();   // ya tenia acceso: se muestra el estado actual
            return;
        }
        boton.disabled = false;

    } catch (error) {
        mostrarAlerta(mensajePago, "error", "No se pudo conectar con el servidor.");
        boton.disabled = false;
    }
}
