package com.orientadorvocacional.app;

import com.orientadorvocacional.config.ConfiguracionSupabase;
import com.orientadorvocacional.excepciones.ContrasenaInvalidaException;
import com.orientadorvocacional.excepciones.CorreoInvalidoException;
import com.orientadorvocacional.excepciones.CorreoYaRegistradoException;
import com.orientadorvocacional.excepciones.RegistroFallidoException;
import com.orientadorvocacional.repositorio.SupabaseRepositorioUsuario;
import com.orientadorvocacional.servicio.CasoUsoRegistrarUsuario;
import com.orientadorvocacional.servicio.SupabaseServicioAutenticacion;

import java.io.IOException;
import java.util.Scanner;

/**
 * Punto de entrada de prueba: permite probar el registro por consola
 * mientras el equipo construye la interfaz grafica real.
 */
public class Main {

    public static void main(String[] args) {
        try {
            ConfiguracionSupabase configuracion = new ConfiguracionSupabase("application.properties");

            CasoUsoRegistrarUsuario casoUsoRegistro = new CasoUsoRegistrarUsuario(
                    new SupabaseServicioAutenticacion(configuracion),
                    new SupabaseRepositorioUsuario(configuracion)
            );

            String correo;
            String contrasena;

            try (Scanner lector = new Scanner(System.in)) {
                System.out.print("Correo: ");
                correo = lector.nextLine();

                System.out.print("Contraseña (mín. 8 caracteres, 1 mayúscula, 1 número): ");
                contrasena = lector.nextLine();
            }

            casoUsoRegistro.ejecutar(correo, contrasena);

            System.out.println("¡Registro exitoso! Revisa la tabla 'usuarios' en Supabase.");

        } catch (CorreoInvalidoException | ContrasenaInvalidaException excepcion) {
            System.out.println("Datos incompletos o inválidos: " + excepcion.getMessage());
        } catch (CorreoYaRegistradoException excepcion) {
            System.out.println("No se pudo registrar: " + excepcion.getMessage());
        } catch (RegistroFallidoException excepcion) {
            System.out.println("Error al registrar: " + excepcion.getMessage());
        } catch (IOException excepcion) {
            System.out.println("Error de configuración: " + excepcion.getMessage());
        }
    }
}