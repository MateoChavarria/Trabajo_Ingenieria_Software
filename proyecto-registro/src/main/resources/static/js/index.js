// Pagina de inicio: el boton principal depende de si hay una sesion iniciada.
// Con sesion no tiene sentido ofrecer "Crear mi cuenta": se lleva a la persona
// a lo siguiente que le corresponde (hacer el test o desbloquear el acceso).
const botonPrincipal = document.getElementById("btnPrincipal");

if (botonPrincipal && obtenerSesion()) {
    botonPrincipal.textContent = "Cargando...";
    botonPrincipal.removeAttribute("href");

    esperarAcceso().then((acceso) => {
        if (acceso === true) {
            botonPrincipal.textContent = "Hacer el test";
            botonPrincipal.href = "test.html";
        } else if (acceso === false) {
            botonPrincipal.textContent = "Desbloquear acceso completo";
            botonPrincipal.href = "pago.html";
        } else {
            botonPrincipal.textContent = "Ir a mi perfil";
            botonPrincipal.href = "perfil.html";
        }
    });
}
