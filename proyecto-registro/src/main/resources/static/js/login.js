document.getElementById("formLogin").addEventListener("submit", async (evento) => {
    evento.preventDefault();

    const correo = document.getElementById("correo").value;
    const contrasena = document.getElementById("contrasena").value;
    const mensajeError = document.getElementById("mensajeError");
    mensajeError.replaceChildren();

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
            // otra pagina, volvemos a ella; solo se aceptan paginas
            // propias de la lista blanca (evita redirecciones abiertas
            // y URLs "javascript:").
            const parametros = new URLSearchParams(window.location.search);
            window.location.href = destinoSeguroDespuesDeLogin(parametros.get("volver"));
        } else {
            mostrarAlerta(mensajeError, "error", await respuesta.text());
        }

    } catch (error) {
        mostrarAlerta(mensajeError, "error", "No se pudo conectar con el servidor.");
    }
});