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

/** Nombres legibles de las areas (los mismos del backend). Los codigos vienen con guion bajo, por ejemplo "ciencias_sociales". */
const NOMBRES_AREAS = {
    ingenieria: "Ingenierías",
    administracion: "Economía, Administración y Negocios",
    "diseño": "Arquitectura y Diseño",
    ciencias_sociales: "Ciencias Sociales",
    educacion: "Educación y Pedagogía",
    humanidades: "Teología, Filosofía y Humanidades",
    derecho: "Derecho y Ciencias Políticas",
    salud: "Ciencias de la Salud"
};

/** Convierte el codigo de un area en texto para mostrar. Si no la conoce, quita los guiones bajos. */
function nombreArea(codigo) {
    if (Object.prototype.hasOwnProperty.call(NOMBRES_AREAS, codigo)) {
        return NOMBRES_AREAS[codigo];
    }
    const texto = String(codigo).replace(/_/g, " ");
    return texto.charAt(0).toUpperCase() + texto.slice(1);
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

function enlaceSimulador(idCarrera) {
    return "simulador.html?id=" + encodeURIComponent(String(idCarrera));
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
