package com.orientadorvocacional.app;

import com.orientadorvocacional.config.ConfiguracionSupabase;
import com.orientadorvocacional.repositorio.SupabaseRepositorioUsuario;
import com.orientadorvocacional.servicio.CasoUsoRegistrarUsuario;
import com.orientadorvocacional.servicio.SupabaseServicioAutenticacion;
import com.orientadorvocacional.vista.VistaRegistro;
import javax.swing.SwingUtilities;

/**
 * Punto de entrada principal de la aplicación.
 * Lanza la interfaz gráfica del Swing.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ConfiguracionSupabase configuracion = new ConfiguracionSupabase("application.properties");

                CasoUsoRegistrarUsuario casoUsoRegistro = new CasoUsoRegistrarUsuario(
                        new SupabaseServicioAutenticacion(configuracion),
                        new SupabaseRepositorioUsuario(configuracion)
                );

                VistaRegistro vista = new VistaRegistro(casoUsoRegistro);
                vista.setVisible(true);

            } catch (Exception e) {
                System.err.println("Error al iniciar la aplicación: " + e.getMessage());
            }
        });
    }
}