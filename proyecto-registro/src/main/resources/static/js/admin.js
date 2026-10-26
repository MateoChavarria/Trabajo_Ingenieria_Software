/**
 * Panel de administracion. Solo la cuenta administradora entra: el servidor
 * lo comprueba en CADA peticion a /api/admin/**; aqui solo se evita mostrar
 * la pantalla a quien no corresponde.
 * Requiere utilidadesDom.js y sesion.js.
 */

const ESTADO_NO_AUTENTICADO = 401;
const ESTADO_SIN_PERMISO = 403;
const ESTADO_SIN_CONTENIDO = 204;
const TARIFA_SIN_DEFINIR = "";

const panel = document.getElementById("panelAdmin");
const contenedorPestanas = document.getElementById("pestanas");
const contenedorMensaje = document.getElementById("mensajeAdmin");
const contenido = document.getElementById("contenidoAdmin");

// ------------------------------------------------------------
// Comunicacion con el servidor
// ------------------------------------------------------------

async function llamarApi(metodo, ruta, cuerpo) {
    const sesion = obtenerSesion();
    if (!sesion) {
        redirigirALogin();
        throw new Error("Sin sesión");
    }

    const opciones = { method: metodo, headers: { "Authorization": "Bearer " + sesion.tokenAcceso } };
    if (cuerpo !== undefined) {
        opciones.headers["Content-Type"] = "application/json";
        opciones.body = JSON.stringify(cuerpo);
    }

    const respuesta = await fetch(ruta, opciones);
    if (respuesta.status === ESTADO_NO_AUTENTICADO) {
        limpiarDatosSesion();
        redirigirALogin();
        throw new Error("Sesión expirada");
    }
    if (respuesta.status === ESTADO_SIN_PERMISO) {
        window.location.href = "index.html";
        throw new Error("Sin permiso");
    }
    if (!respuesta.ok) {
        throw new Error((await respuesta.text()) || "No se pudo completar la operación.");
    }
    return respuesta.status === ESTADO_SIN_CONTENIDO ? null : respuesta.json().catch(() => null);
}

function avisar(tipo, texto) {
    mostrarAlerta(contenedorMensaje, tipo, texto);
}

function limpiarAviso() {
    contenedorMensaje.replaceChildren();
}

/** Ejecuta una accion del panel y muestra el error si falla. Devuelve true si salio bien. */
async function intentar(accion, mensajeExito) {
    try {
        await accion();
        if (mensajeExito) avisar("exito", mensajeExito);
        return true;
    } catch (error) {
        avisar("error", error.message);
        return false;
    }
}

// ------------------------------------------------------------
// Piezas de interfaz reutilizables
// ------------------------------------------------------------

function crearTabla(columnas, filas) {
    if (filas.length === 0) {
        return crearElemento("p", "sub", "No hay registros todavía.");
    }
    const tabla = crearElemento("table", "tabla");
    const encabezado = crearElemento("tr");
    columnas.forEach((columna) => encabezado.appendChild(crearElemento("th", null, columna.titulo)));
    tabla.appendChild(crearElemento("thead")).appendChild(encabezado);

    const cuerpo = crearElemento("tbody");
    filas.forEach((fila) => {
        const tr = crearElemento("tr");
        columnas.forEach((columna) => {
            const celda = crearElemento("td");
            celda.setAttribute("data-etiqueta", columna.titulo);
            const valor = columna.valor(fila);
            if (valor instanceof Node) celda.appendChild(valor);
            else celda.textContent = valor === null || valor === undefined ? "" : String(valor);
            tr.appendChild(celda);
        });
        cuerpo.appendChild(tr);
    });
    tabla.appendChild(cuerpo);

    const envoltorio = crearElemento("div", "tabla-scroll");
    envoltorio.appendChild(tabla);
    return envoltorio;
}

function crearBoton(texto, clase, alHacerClic) {
    const boton = crearElemento("button", clase, texto);
    boton.type = "button";
    boton.addEventListener("click", alHacerClic);
    return boton;
}

function crearBotonesDeFila(alEditar, alEliminar) {
    const grupo = crearElemento("div", "acciones-fila");
    grupo.append(
        crearBoton("Editar", "btn outline btn-mini", alEditar),
        crearBoton("Eliminar", "btn outline btn-mini peligro", alEliminar)
    );
    return grupo;
}

/**
 * Formulario generico. campos: [{ nombre, etiqueta, tipo, opciones, requerido }].
 * tipo: texto | parrafo | numero | lista | casilla.
 */
function crearFormulario(titulo, campos, valores, alGuardar, alCancelar) {
    const formulario = crearElemento("form", "form-admin");
    formulario.noValidate = true;
    formulario.appendChild(crearElemento("h3", null, titulo));

    const controles = {};
    campos.forEach((campo) => {
        const etiqueta = crearElemento("label", null, campo.etiqueta);
        const control = crearControl(campo, valores[campo.nombre]);
        controles[campo.nombre] = control;
        if (campo.tipo === "casilla") {
            etiqueta.prepend(control);
            etiqueta.className = "casilla";
            formulario.appendChild(etiqueta);
        } else {
            formulario.append(etiqueta, control);
        }
    });

    const botones = crearElemento("div", "acciones-fila");
    const guardar = crearElemento("button", "btn sun", "Guardar");
    guardar.type = "submit";
    botones.append(guardar, crearBoton("Cancelar", "btn outline", alCancelar));
    formulario.appendChild(botones);

    formulario.addEventListener("submit", async (evento) => {
        evento.preventDefault();
        guardar.disabled = true;
        const datos = leerValores(campos, controles);
        await alGuardar(datos);
        guardar.disabled = false;
    });
    return formulario;
}

function crearControl(campo, valorInicial) {
    if (campo.tipo === "parrafo") {
        const area = crearElemento("textarea");
        area.rows = 4;
        area.value = valorInicial || "";
        return area;
    }
    if (campo.tipo === "lista") {
        const lista = crearElemento("select");
        campo.opciones.forEach(([valor, texto]) => {
            const opcion = crearElemento("option", null, texto);
            opcion.value = String(valor);
            lista.appendChild(opcion);
        });
        if (valorInicial !== undefined && valorInicial !== null) lista.value = String(valorInicial);
        return lista;
    }
    const entrada = crearElemento("input");
    if (campo.tipo === "casilla") {
        entrada.type = "checkbox";
        entrada.checked = valorInicial === true;
        return entrada;
    }
    entrada.type = campo.tipo === "numero" ? "number" : "text";
    if (campo.tipo === "numero") entrada.min = "0";
    entrada.value = valorInicial === undefined || valorInicial === null ? "" : String(valorInicial);
    return entrada;
}

function leerValores(campos, controles) {
    const datos = {};
    campos.forEach((campo) => {
        const control = controles[campo.nombre];
        if (campo.tipo === "casilla") datos[campo.nombre] = control.checked;
        else if (campo.tipo === "numero") datos[campo.nombre] = control.value === "" ? null : Number(control.value);
        else if (campo.tipo === "lista" && campo.numerica) datos[campo.nombre] = Number(control.value);
        else datos[campo.nombre] = control.value;
    });
    return datos;
}

// ------------------------------------------------------------
// Pestanas
// ------------------------------------------------------------

const SECCIONES = [
    { id: "resumen", titulo: "Resumen", dibujar: dibujarResumen },
    { id: "usuarios", titulo: "Usuarios", dibujar: dibujarUsuarios },
    { id: "pagos", titulo: "Pagos", dibujar: dibujarPagos },
    { id: "carreras", titulo: "Carreras", dibujar: dibujarCarreras },
    { id: "universidades", titulo: "Universidades", dibujar: dibujarUniversidades },
    { id: "preguntas", titulo: "Preguntas del test", dibujar: dibujarPreguntas }
];

function construirPestanas() {
    SECCIONES.forEach((seccion) => {
        const boton = crearBoton(seccion.titulo, "pestana", () => abrirSeccion(seccion.id));
        boton.id = "pestana-" + seccion.id;
        boton.setAttribute("role", "tab");
        contenedorPestanas.appendChild(boton);
    });
}

async function abrirSeccion(idSeccion) {
    limpiarAviso();
    SECCIONES.forEach((seccion) => {
        const activa = seccion.id === idSeccion;
        const boton = document.getElementById("pestana-" + seccion.id);
        boton.classList.toggle("activa", activa);
        boton.setAttribute("aria-selected", String(activa));
    });
    contenido.replaceChildren(crearElemento("p", "sub", "Cargando..."));

    const seccion = SECCIONES.find((s) => s.id === idSeccion);
    const dibujada = await intentar(() => seccion.dibujar());
    if (!dibujada) contenido.replaceChildren();
}

// ------------------------------------------------------------
// Resumen, usuarios y pagos (solo lectura)
// ------------------------------------------------------------

async function dibujarResumen() {
    const resumen = await llamarApi("GET", "/api/admin/resumen");
    const indicadores = [
        ["Usuarios registrados", resumen.usuarios],
        ["Usuarios con acceso completo", resumen.usuariosConAcceso],
        ["Pagos exitosos", resumen.pagosExitosos],
        ["Ingresos totales", formatoPesos(resumen.ingresosTotales)],
        ["Tests realizados", resumen.testsRealizados],
        ["Carreras", resumen.carreras],
        ["Universidades", resumen.universidades],
        ["Preguntas", resumen.preguntas]
    ];
    const rejilla = crearElemento("div", "indicadores");
    indicadores.forEach(([etiqueta, valor]) => {
        const tarjeta = crearElemento("div", "indicador");
        tarjeta.append(crearElemento("strong", null, valor), crearElemento("span", null, etiqueta));
        rejilla.appendChild(tarjeta);
    });
    contenido.replaceChildren(rejilla);
}

function formatoFecha(texto) {
    const fecha = new Date(texto);
    return Number.isNaN(fecha.getTime()) ? "" : fecha.toLocaleString("es-CO");
}

async function dibujarUsuarios() {
    const usuarios = await llamarApi("GET", "/api/admin/usuarios");
    contenido.replaceChildren(crearTabla([
        { titulo: "Correo", valor: (u) => u.correo },
        { titulo: "Registro", valor: (u) => formatoFecha(u.fechaRegistro) },
        { titulo: "Estado", valor: (u) => u.estadoCuenta },
        { titulo: "Acceso completo", valor: (u) => (u.accesoCompleto ? "Sí" : "No") }
    ], usuarios));
}

async function dibujarPagos() {
    const pagos = await llamarApi("GET", "/api/admin/pagos");
    contenido.replaceChildren(crearTabla([
        { titulo: "Fecha", valor: (p) => formatoFecha(p.fecha) },
        { titulo: "Correo", valor: (p) => p.correo },
        { titulo: "Monto", valor: (p) => formatoPesos(p.monto) },
        { titulo: "Estado", valor: (p) => p.estado },
        { titulo: "Referencia", valor: (p) => p.referencia },
        { titulo: "Motivo del fallo", valor: (p) => p.motivoFallo || "" }
    ], pagos));
}

// ------------------------------------------------------------
// Carreras
// ------------------------------------------------------------

function camposCarrera(universidades) {
    return [
        { nombre: "nombre", etiqueta: "Nombre", tipo: "texto" },
        { nombre: "descripcion", etiqueta: "Descripción", tipo: "parrafo" },
        { nombre: "campoLaboral", etiqueta: "Campo laboral", tipo: "parrafo" },
        { nombre: "areaCategoria", etiqueta: "Área", tipo: "lista",
          opciones: Object.entries(NOMBRES_AREAS) },
        { nombre: "duracionSemestres", etiqueta: "Duración en semestres", tipo: "numero" },
        { nombre: "tarifaSemestre", etiqueta: "Tarifa por semestre en pesos (opcional)", tipo: "numero" },
        { nombre: "universidadId", etiqueta: "Universidad", tipo: "lista", numerica: true,
          opciones: universidades.map((u) => [u.id, u.nombre]) }
    ];
}

async function dibujarCarreras() {
    const [carreras, universidades] = await Promise.all([
        llamarApi("GET", "/api/admin/carreras"),
        llamarApi("GET", "/api/admin/universidades")
    ]);
    const nombreUniversidad = new Map(universidades.map((u) => [u.id, u.nombre]));

    const abrirFormulario = (carrera) => {
        const esNueva = !carrera;
        const formulario = crearFormulario(
            esNueva ? "Nueva carrera" : "Editar carrera",
            camposCarrera(universidades),
            carrera || { areaCategoria: "ingenieria", universidadId: universidades[0] && universidades[0].id },
            async (datos) => {
                const ruta = esNueva ? "/api/admin/carreras" : "/api/admin/carreras/" + carrera.id;
                const guardada = await intentar(
                    () => llamarApi(esNueva ? "POST" : "PUT", ruta, datos),
                    esNueva ? "Carrera creada." : "Carrera actualizada.");
                if (guardada) await dibujarCarreras();
            },
            () => dibujarCarreras());
        contenido.replaceChildren(formulario);
    };

    const eliminar = async (carrera) => {
        if (!window.confirm("¿Eliminar la carrera \"" + carrera.nombre + "\"?")) return;
        await intentar(() => llamarApi("DELETE", "/api/admin/carreras/" + carrera.id), "Carrera eliminada.");
        await dibujarCarreras();
    };

    const tabla = crearTabla([
        { titulo: "Nombre", valor: (c) => c.nombre },
        { titulo: "Universidad", valor: (c) => nombreUniversidad.get(c.universidadId) || "" },
        { titulo: "Área", valor: (c) => nombreArea(c.areaCategoria) },
        { titulo: "Semestres", valor: (c) => c.duracionSemestres },
        { titulo: "Acciones", valor: (c) => crearBotonesDeFila(() => abrirFormulario(c), () => eliminar(c)) }
    ], carreras);

    contenido.replaceChildren(
        crearBoton("Nueva carrera", "btn sun", () => abrirFormulario(null)),
        tabla);
}

// ------------------------------------------------------------
// Universidades
// ------------------------------------------------------------

const CAMPOS_UNIVERSIDAD = [
    { nombre: "nombre", etiqueta: "Nombre", tipo: "texto" },
    { nombre: "municipio", etiqueta: "Municipio", tipo: "texto" },
    { nombre: "caracter", etiqueta: "Carácter", tipo: "lista", opciones: [["Pública", "Pública"], ["Privada", "Privada"]] },
    { nombre: "tieneInformacionCompleta", etiqueta: "Tiene información completa (carreras y plan de estudios)", tipo: "casilla" }
];

async function dibujarUniversidades() {
    const universidades = await llamarApi("GET", "/api/admin/universidades");

    const abrirFormulario = (universidad) => {
        const esNueva = !universidad;
        const formulario = crearFormulario(
            esNueva ? "Nueva universidad" : "Editar universidad",
            CAMPOS_UNIVERSIDAD,
            universidad || { caracter: "Privada" },
            async (datos) => {
                const ruta = esNueva ? "/api/admin/universidades" : "/api/admin/universidades/" + universidad.id;
                const guardada = await intentar(
                    () => llamarApi(esNueva ? "POST" : "PUT", ruta, datos),
                    esNueva ? "Universidad creada." : "Universidad actualizada.");
                if (guardada) await dibujarUniversidades();
            },
            () => dibujarUniversidades());
        contenido.replaceChildren(formulario);
    };

    const eliminar = async (universidad) => {
        if (!window.confirm("¿Eliminar la universidad \"" + universidad.nombre + "\"?")) return;
        await intentar(() => llamarApi("DELETE", "/api/admin/universidades/" + universidad.id), "Universidad eliminada.");
        await dibujarUniversidades();
    };

    const tabla = crearTabla([
        { titulo: "Nombre", valor: (u) => u.nombre },
        { titulo: "Municipio", valor: (u) => u.municipio },
        { titulo: "Carácter", valor: (u) => u.caracter },
        { titulo: "Información completa", valor: (u) => (u.tieneInformacionCompleta ? "Sí" : "No") },
        { titulo: "Acciones", valor: (u) => crearBotonesDeFila(() => abrirFormulario(u), () => eliminar(u)) }
    ], universidades);

    contenido.replaceChildren(
        crearBoton("Nueva universidad", "btn sun", () => abrirFormulario(null)),
        tabla);
}

// ------------------------------------------------------------
// Preguntas del test (solo se edita el texto)
// ------------------------------------------------------------

function crearEditorDeTexto(texto, rutaGuardar, mensajeExito) {
    const fila = crearElemento("div", "editor-texto");
    const entrada = crearElemento("input");
    entrada.type = "text";
    entrada.value = texto;
    const guardar = crearBoton("Guardar", "btn outline btn-mini", async () => {
        guardar.disabled = true;
        await intentar(() => llamarApi("PUT", rutaGuardar, { texto: entrada.value }), mensajeExito);
        guardar.disabled = false;
    });
    fila.append(entrada, guardar);
    return fila;
}

async function dibujarPreguntas() {
    const preguntas = await llamarApi("GET", "/api/admin/preguntas");
    const lista = crearElemento("div", "lista-preguntas");

    preguntas.forEach((pregunta, indice) => {
        const tarjeta = crearElemento("div", "pregunta-admin");
        tarjeta.append(
            crearElemento("small", null, "Pregunta " + (indice + 1) + " · " + pregunta.categoria),
            crearEditorDeTexto(pregunta.texto, "/api/admin/preguntas/" + pregunta.id, "Pregunta actualizada."));
        pregunta.opciones.forEach((opcion) => {
            const bloque = crearElemento("div", "opcion-admin");
            bloque.append(
                crearElemento("small", null, nombreArea(opcion.area)),
                crearEditorDeTexto(opcion.texto, "/api/admin/opciones/" + opcion.id, "Opción actualizada."));
            tarjeta.appendChild(bloque);
        });
        lista.appendChild(tarjeta);
    });
    contenido.replaceChildren(lista);
}

// ------------------------------------------------------------
// Arranque: solo se muestra el panel si el servidor confirma que es admin
// ------------------------------------------------------------

async function iniciarPanel() {
    const respuesta = await llamarApi("GET", "/api/yo/admin").catch(() => null);
    if (!respuesta || respuesta.esAdmin !== true) {
        window.location.href = "index.html";
        return;
    }
    construirPestanas();
    panel.hidden = false;
    await abrirSeccion("resumen");
}

if (obtenerSesion()) {
    iniciarPanel();
}
