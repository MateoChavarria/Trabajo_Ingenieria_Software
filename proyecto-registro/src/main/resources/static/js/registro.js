document.getElementById("formRegistro").addEventListener("submit", async (evento) => {
    evento.preventDefault();

    const correo = document.getElementById("correo").value;
    const contrasena = document.getElementById("contrasena").value;
    const mensajeError = document.getElementById("mensajeError");
    mensajeError.innerHTML = "";

    try {
        const respuesta = await fetch("/api/auth/registro", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ correo, contrasena })
        });

        if (respuesta.ok) {
            window.location.href = "login.html";
        } else {
            const textoError = await respuesta.text();
            mensajeError.innerHTML = `<div class="alerta error">${textoError}</div>`;
        }

    } catch (error) {
        mensajeError.innerHTML = `<div class="alerta error">No se pudo conectar con el servidor.</div>`;
    }
});