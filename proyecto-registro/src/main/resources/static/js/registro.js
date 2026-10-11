document.getElementById("formRegistro").addEventListener("submit", async (evento) => {
    evento.preventDefault();

    const correo = document.getElementById("correo").value;
    const contrasena = document.getElementById("contrasena").value;
    const mensajeError = document.getElementById("mensajeError");
    mensajeError.replaceChildren();

    try {
        const respuesta = await fetch("/api/auth/registro", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ correo, contrasena })
        });

        if (respuesta.ok) {
            window.location.href = "login.html";
        } else {
            mostrarAlerta(mensajeError, "error", await respuesta.text());
        }

    } catch (error) {
        mostrarAlerta(mensajeError, "error", "No se pudo conectar con el servidor.");
    }
});