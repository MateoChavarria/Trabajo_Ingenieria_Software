package com.orientadorvocacional.vista;

import javax.swing.*;
import java.awt.*;

public class VistaLogin extends JFrame {

    public VistaLogin() {
        setTitle("Iniciar Sesión");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        JLabel lblMensaje = new JLabel("¡Registro exitoso! Pantalla de Login.");
        lblMensaje.setFont(new Font("SansSerif", Font.BOLD, 13));
        panel.add(lblMensaje);

        add(panel);
    }
}