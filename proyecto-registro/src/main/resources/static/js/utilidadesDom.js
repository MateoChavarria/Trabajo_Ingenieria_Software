/**
 * Utilidades para construir el DOM de forma segura.
 * Regla: NUNCA insertar datos con innerHTML; usar textContent (evita XSS).
 */

function crearElemento(etiqueta, clase, texto) {
    const elemento = document.createElement(etiqueta);
    if (clase) elemento.className = clase;
    if (texto !== undefined && texto !== null) elemento.textContent = String(texto);
    return elemento;
}

function porcentajeSeguro(valor) {
    const numero = Number(valor);
    if (!Number.isFinite(numero)) return 0;
    return Math.min(100, Math.max(0, Math.round(numero)));
}

function crearBarraAfinidad(etiqueta, valor) {
    const porcentaje = porcentajeSeguro(valor);

    const barra = crearElemento("div", "bar");
    const encabezado = crearElemento("div");
    encabezado.append(crearElemento("b", null, etiqueta), crearElemento("b", null, porcentaje + "%"));

    const pista = crearElemento("span");
    const relleno = crearElemento("i");
    relleno.style.width = porcentaje + "%";
    pista.appendChild(relleno);

    barra.append(encabezado, pista);
    return barra;
}

function mostrarAlerta(contenedor, tipo, texto) {
    contenedor.replaceChildren(crearElemento("div", "alerta " + tipo, texto));
}

function enlaceDetalleCarrera(idCarrera) {
    return "carrera-detalle.html?id=" + encodeURIComponent(String(idCarrera));
}

function leerJsonLocal(clave) {
    const datos = localStorage.getItem(clave);
    if (!datos) return null;
    try {
        return JSON.parse(datos);
    } catch (error) {
        localStorage.removeItem(clave);
        return null;
    }
}