package com.orientadorvocacional.vista;

import com.orientadorvocacional.excepciones.CredencialesInvalidasException;
import com.orientadorvocacional.excepciones.InicioSesionFallidoException;
import com.orientadorvocacional.modelo.SesionUsuario;
import com.orientadorvocacional.servicio.CasoUsoIniciarSesion;

import javax.swing.*;
import java.awt.*;

public class VistaLogin extends JFrame {

    private JTextField txtCorreo;
    private JPasswordField txtContrasena;
    private JButton btnIniciarSesion;
    private JLabel lblError;

    private final CasoUsoIniciarSesion casoUsoLogin;

    public VistaLogin(CasoUsoIniciarSesion casoUsoLogin) {
        this.casoUsoLogin = casoUsoLogin;

        setTitle("Orientador Vocacional - Iniciar sesión");
        setSize(380, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponentes();
    }

    private void initComponentes() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Correo electrónico:"), gbc);

        txtCorreo = new JTextField(20);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(txtCorreo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Contraseña:"), gbc);

        txtContrasena = new JPasswordField(20);
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(txtContrasena, gbc);

        lblError = new JLabel(" ", SwingConstants.CENTER);
        lblError.setForeground(Color.RED);
        lblError.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(lblError, gbc);

        btnIniciarSesion = new JButton("Iniciar sesión");
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.insets = new Insets(10, 4, 4, 4);
        panel.add(btnIniciarSesion, gbc);

        add(panel);

        btnIniciarSesion.addActionListener(e -> iniciarSesion());
    }

    private void iniciarSesion() {
        String correo = txtCorreo.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());

        // Validación de campos vacíos, tal como pide la tarea técnica.
        if (correo.isEmpty() || contrasena.isEmpty()) {
            lblError.setText("Completa correo y contraseña.");
            return;
        }

        try {
            SesionUsuario sesion = casoUsoLogin.ejecutar(correo, contrasena);

            JOptionPane.showMessageDialog(this,
                    "¡Bienvenido! Sesión válida hasta las " + sesion.getFechaExpiracion(),
                    "Inicio de sesión exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

            // Aquí, en un sprint futuro, se abrirá la pantalla principal
            // (por ejemplo, el test vocacional) pasándole la sesión.
            this.dispose();

        } catch (CredencialesInvalidasException ex) {
            // Mensaje genérico, sin decir si falló el correo o la contraseña.
            lblError.setText(ex.getMessage());
        } catch (InicioSesionFallidoException ex) {
            lblError.setText("Error: " + ex.getMessage());
        }
    }
}