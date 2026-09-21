document.getElementById("formLogin").addEventListener("submit", async (evento) => {
    evento.preventDefault();

    const correo = document.getElementById("correo").value;
    const contrasena = document.getElementById("contrasena").value;
    const mensajeError = document.getElementById("mensajeError");
    mensajeError.textContent = "";

    try {
        const respuesta = await fetch("/api/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ correo, contrasena })
        });

        if (respuesta.ok) {
            // El backend devuelve la SesionUsuario completa (tokenAcceso,
            // idUsuario, fechaExpiracion). La guardamos en localStorage
            // del navegador para no pedir login en cada pantalla, y
            // porque test.js va a necesitar el idUsuario para asociar
            // el resultado del test a este usuario.
            const sesion = await respuesta.json();
            localStorage.setItem("sesion", JSON.stringify(sesion));

            window.location.href = "test.html";
        } else {
            // Mensaje generico a proposito (definido en el backend):
            // nunca se dice si fallo el correo o la contraseña.
            const textoError = await respuesta.text();
            mensajeError.textContent = textoError;
        }

    } catch (error) {
        mensajeError.textContent = "No se pudo conectar con el servidor.";
    }
});
