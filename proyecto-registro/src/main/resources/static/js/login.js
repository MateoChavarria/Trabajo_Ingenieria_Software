document.getElementById("formLogin").addEventListener("submit", async (evento) => {
    evento.preventDefault();

    const correo = document.getElementById("correo").value;
    const contrasena = document.getElementById("contrasena").value;
    const mensajeError = document.getElementById("mensajeError");
    mensajeError.innerHTML = "";

    try {
        const respuesta = await fetch("/api/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ correo, contrasena })
        });

        if (respuesta.ok) {
            const sesion = await respuesta.json();
            localStorage.setItem("sesion", JSON.stringify(sesion));

            // Si llegamos aqui porque requerirSesion() nos mando desde
            // otra pagina (test, perfil, carreras), volvemos a ella en
            // vez de ir siempre al test por defecto.
            const parametros = new URLSearchParams(window.location.search);
            const volver = parametros.get("volver");
            window.location.href = volver ? volver : "test.html";
        } else {
            const textoError = await respuesta.text();
            mensajeError.innerHTML = `<div class="alerta error">${textoError}</div>`;
        }

    } catch (error) {
        mensajeError.innerHTML = `<div class="alerta error">No se pudo conectar con el servidor.</div>`;
    }
});