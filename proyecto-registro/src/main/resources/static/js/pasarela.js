// Pasarela de pagos SIMULADA. Muestra el formulario de tarjeta, manda los
// datos al servidor (que decide si el pago se aprueba segun la tarjeta de
// prueba) y lleva a la pantalla de confirmacion. Los datos de la tarjeta
// nunca se guardan en el navegador. Todo el texto se inserta con textContent.

const parametrosPasarela = new URLSearchParams(window.location.search);
const referenciaPago = parametrosPasarela.get("ref");
const referenciaEsValida = /^PAY-[0-9A-F]{12}$/.test(referenciaPago || "");

const mensajePasarela = document.getElementById("mensajePasarela");
const tarjetaPasarela = document.getElementById("tarjetaPasarela");
const formPago = document.getElementById("formPago");
const campoNombre = document.getElementById("nombreTitular");
const campoNumero = document.getElementById("numeroTarjeta");
const campoVencimiento = document.getElementById("vencimiento");
const campoCvv = document.getElementById("cvv");
const botonPagar = document.getElementById("btnPagar");
const botonCancelar = document.getElementById("btnCancelar");

/** Tarjetas de prueba que entiende la pasarela simulada (las mismas del backend). */
const TARJETAS_DE_PRUEBA = [
    { numero: "4242 4242 4242 4242", resultado: "Pago aprobado" },
    { numero: "4000 0000 0000 0002", resultado: "Rechazada: fondos insuficientes" },
    { numero: "4000 0000 0000 0069", resultado: "Rechazada por el banco emisor" }
];

if (!referenciaEsValida) {
    mostrarAlerta(mensajePasarela, "error", "No se encontró el pago. Vuelve a la pantalla de pago e inténtalo de nuevo.");
    agregarEnlace("pago.html", "Ir a la pantalla de pago");
} else {
    cargarPago();
}

function agregarEnlace(destino, texto) {
    const enlace = crearElemento("a", "btn sun", texto);
    enlace.href = destino;
    mensajePasarela.appendChild(enlace);
}

async function cargarPago() {
    try {
        const respuesta = await fetchAutenticado("/api/pagos/" + encodeURIComponent(referenciaPago));
        if (!respuesta.ok) {
            mostrarAlerta(mensajePasarela, "error", (await respuesta.text()) || "No se pudo cargar el pago.");
            agregarEnlace("pago.html", "Ir a la pantalla de pago");
            return;
        }

        const pago = await respuesta.json();
        if (pago.estado !== "pendiente") {
            // El pago ya tiene resultado: no se vuelve a cobrar, se muestra el resultado.
            irAConfirmacion();
            return;
        }

        document.getElementById("textoReferencia").textContent = pago.referencia;
        document.getElementById("textoMonto").textContent = formatoPesos(pago.monto) + " " + pago.moneda;
        dibujarTarjetasDePrueba();
        tarjetaPasarela.hidden = false;
        campoNombre.focus();

    } catch (error) {
        mostrarAlerta(mensajePasarela, "error", "No se pudo conectar con el servidor.");
    }
}

function irAConfirmacion() {
    window.location.href = "confirmacion.html?ref=" + encodeURIComponent(referenciaPago) + sufijoVolver();
}

// ---------- Tarjetas de prueba (con boton para autocompletar) ----------

function vencimientoDeEjemplo() {
    const fecha = new Date();
    const mes = String(fecha.getMonth() + 1).padStart(2, "0");
    const anio = String((fecha.getFullYear() + 2) % 100).padStart(2, "0");
    return mes + "/" + anio;
}

function dibujarTarjetasDePrueba() {
    const tabla = document.getElementById("tablaTarjetasPrueba");
    tabla.replaceChildren();

    TARJETAS_DE_PRUEBA.forEach((tarjeta) => {
        const boton = crearElemento("button", "btn outline btn-mini", "Usar");
        boton.type = "button";
        boton.addEventListener("click", () => {
            if (!campoNombre.value.trim()) campoNombre.value = "Estudiante de Prueba";
            campoNumero.value = tarjeta.numero;
            campoVencimiento.value = vencimientoDeEjemplo();
            campoCvv.value = "123";
        });

        const fila = crearElemento("tr");
        const celdaBoton = crearElemento("td");
        celdaBoton.appendChild(boton);
        fila.append(crearElemento("td", null, tarjeta.numero), crearElemento("td", null, tarjeta.resultado), celdaBoton);
        tabla.appendChild(fila);
    });
}

// ---------- Ayudas de escritura ----------

campoNumero.addEventListener("input", () => {
    const digitos = campoNumero.value.replace(/\D/g, "").slice(0, 19);
    campoNumero.value = digitos.replace(/(.{4})/g, "$1 ").trim();
});

campoVencimiento.addEventListener("input", () => {
    const digitos = campoVencimiento.value.replace(/\D/g, "").slice(0, 4);
    campoVencimiento.value = digitos.length > 2 ? digitos.slice(0, 2) + "/" + digitos.slice(2) : digitos;
});

campoCvv.addEventListener("input", () => {
    campoCvv.value = campoCvv.value.replace(/\D/g, "").slice(0, 4);
});

// ---------- Pagar / Cancelar ----------

function bloquearBotones(bloqueado) {
    botonPagar.disabled = bloqueado;
    botonCancelar.disabled = bloqueado;
}

formPago.addEventListener("submit", async (evento) => {
    evento.preventDefault();
    mensajePasarela.replaceChildren();
    bloquearBotones(true);   // evita cobrar dos veces si se pulsa dos veces

    try {
        const respuesta = await fetchAutenticado("/api/pagos/" + encodeURIComponent(referenciaPago) + "/procesar", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                nombreTitular: campoNombre.value,
                numeroTarjeta: campoNumero.value,
                vencimiento: campoVencimiento.value,
                cvv: campoCvv.value
            })
        });

        // 200: el pago quedo exitoso o fallido. 409: ya estaba resuelto. En ambos casos se muestra el resultado.
        if (respuesta.ok || respuesta.status === 409) {
            irAConfirmacion();
            return;
        }

        // 400: datos de tarjeta mal escritos; el pago sigue pendiente y puede corregirlos.
        mostrarAlerta(mensajePasarela, "error", (await respuesta.text()) || "No se pudo procesar el pago.");
        bloquearBotones(false);

    } catch (error) {
        mostrarAlerta(mensajePasarela, "error", "No se pudo conectar con el servidor.");
        bloquearBotones(false);
    }
});

botonCancelar.addEventListener("click", async () => {
    bloquearBotones(true);
    try {
        await fetchAutenticado("/api/pagos/" + encodeURIComponent(referenciaPago) + "/cancelar", { method: "POST" });
        irAConfirmacion();
    } catch (error) {
        mostrarAlerta(mensajePasarela, "error", "No se pudo cancelar el pago. Intenta de nuevo.");
        bloquearBotones(false);
    }
});
