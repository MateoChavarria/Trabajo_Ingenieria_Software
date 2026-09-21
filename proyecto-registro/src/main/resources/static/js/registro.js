// Toma el formulario y evita que la pagina se recargue al enviarlo
// (comportamiento por defecto de un <form> en HTML).
document.getElementById("formRegistro").addEventListener("submit", async (evento) => {
    evento.preventDefault();

    const correo = document.getElementById("correo").value;
    const contrasena = document.getElementById("contrasena").value;
    const mensajeError = document.getElementById("mensajeError");
    mensajeError.textContent = "";

    try {
        const respuesta = await fetch("/api/auth/registro", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ correo, contrasena })
        });

        if (respuesta.ok) {
            // Registro exitoso: mandamos al usuario a iniciar sesion.
            window.location.href = "login.html";
        } else {
            // El backend manda el mensaje de error como texto plano
            // (correo invalido, contraseña invalida, correo ya registrado, etc.)
            const textoError = await respuesta.text();
            mensajeError.textContent = textoError;
        }

    } catch (error) {
        mensajeError.textContent = "No se pudo conectar con el servidor.";
    }
});
