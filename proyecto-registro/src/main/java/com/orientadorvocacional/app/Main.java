package com.orientadorvocacional.app;

import com.orientadorvocacional.config.ConfiguracionSupabase;
import com.orientadorvocacional.repositorio.SupabaseRepositorioUsuario;
import com.orientadorvocacional.servicio.CasoUsoIniciarSesion;
import com.orientadorvocacional.servicio.CasoUsoRegistrarUsuario;
import com.orientadorvocacional.servicio.SupabaseServicioAutenticacion;
import com.orientadorvocacional.vista.VistaRegistro;
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

                VistaRegistro vista = new VistaRegistro(casoUsoRegistro, casoUsoLogin);
                vista.setVisible(true);

            } catch (Exception e) {
                System.err.println("Error al iniciar la aplicación: " + e.getMessage());
            }
        });
    }
}