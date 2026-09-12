package com.orientadorvocacional.app;

import com.orientadorvocacional.config.ConfiguracionSupabase;
import com.orientadorvocacional.modelo.SesionUsuario;
import com.orientadorvocacional.repositorio.SupabaseRepositorioUsuario;
import com.orientadorvocacional.servicio.*;
import com.orientadorvocacional.vista.VistaLogin;
import com.orientadorvocacional.vista.VistaRegistro;
import com.orientadorvocacional.vista.VistaTestVocacional;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ConfiguracionSupabase configuracion = new ConfiguracionSupabase("application.properties");

                SupabaseServicioAutenticacion servicioAutenticacion =
                        new SupabaseServicioAutenticacion(configuracion);

                CasoUsoRegistrarUsuario casoUsoRegistro = new CasoUsoRegistrarUsuario(
                        servicioAutenticacion,
                        new SupabaseRepositorioUsuario(configuracion)
                );

                CasoUsoIniciarSesion casoUsoLogin = new CasoUsoIniciarSesion(servicioAutenticacion);
                CasoUsoObtenerCuestionario casoUsoTest =
                        new CasoUsoObtenerCuestionario(new SupabaseRepositorioPreguntas(configuracion));

                GestorSesionLocal gestorSesion = new GestorSesionLocal();
                SesionUsuario sesionGuardada = gestorSesion.recuperar();

                // Bloqueo + redirección automática:
                // si hay una sesión válida guardada, saltamos directo al
                // test vocacional (lo último que tendría sentido consultar);
                // si no hay sesión, la única puerta de entrada es Registro/Login.
                if (sesionGuardada != null) {
                    VistaTestVocacional vistaTest =
                            new VistaTestVocacional(casoUsoTest, sesionGuardada);
                    vistaTest.setVisible(true);
                } else {
                    VistaRegistro vista = new VistaRegistro(casoUsoRegistro, casoUsoLogin);
                    vista.setVisible(true);
                }

            } catch (Exception e) {
                System.err.println("Error al iniciar la aplicación: " + e.getMessage());
            }
        });
    }
}