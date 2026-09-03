package com.orientadorvocacional.vista;

import com.orientadorvocacional.excepciones.ContrasenaInvalidaException;
import com.orientadorvocacional.excepciones.CorreoInvalidoException;
import com.orientadorvocacional.excepciones.CorreoYaRegistradoException;
import com.orientadorvocacional.excepciones.RegistroFallidoException;
import com.orientadorvocacional.servicio.CasoUsoRegistrarUsuario;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class VistaRegistro extends JFrame {

    private JTextField txtCorreo;
    private JPasswordField txtContrasena;
    private JButton btnRegistrar;
    private JLabel lblErrorCorreo;
    private JLabel lblErrorContrasena;
    private JLabel lblErrorServidor;

    private final CasoUsoRegistrarUsuario casoUsoRegistro;

    public VistaRegistro(CasoUsoRegistrarUsuario casoUsoRegistro) {
        this.casoUsoRegistro = casoUsoRegistro;

        setTitle("Orientador Vocacional - Registro");
        setSize(420, 380);
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

        // Correo
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Correo electrónico:"), gbc);

        txtCorreo = new JTextField(20);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(txtCorreo, gbc);

        lblErrorCorreo = new JLabel(" ");
        lblErrorCorreo.setForeground(Color.RED);
        lblErrorCorreo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(lblErrorCorreo, gbc);

        // Contraseña
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Contraseña:"), gbc);

        txtContrasena = new JPasswordField(20);
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(txtContrasena, gbc);

        lblErrorContrasena = new JLabel(" ");
        lblErrorContrasena.setForeground(Color.RED);
        lblErrorContrasena.setFont(new Font("SansSerif", Font.PLAIN, 11));
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(lblErrorContrasena, gbc);

        // Error servidor
        lblErrorServidor = new JLabel(" ", SwingConstants.CENTER);
        lblErrorServidor.setForeground(Color.RED);
        lblErrorServidor.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(lblErrorServidor, gbc);

        // Botón
        btnRegistrar = new JButton("Crear Cuenta");
        btnRegistrar.setEnabled(false);
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.insets = new Insets(10, 4, 4, 4);
        panel.add(btnRegistrar, gbc);

        add(panel);

        // Listener validación en tiempo real
        DocumentListener listenerValidacion = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validarCampos(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validarCampos(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validarCampos(); }
        };

        txtCorreo.getDocument().addDocumentListener(listenerValidacion);
        txtContrasena.getDocument().addDocumentListener(listenerValidacion);

        btnRegistrar.addActionListener(e -> registrarUsuario());
    }

    private void validarCampos() {
        String correo = txtCorreo.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());

        boolean correoValido = correo.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
        boolean contrasenaValida = contrasena.length() >= 8 
                && contrasena.chars().anyMatch(Character::isUpperCase) 
                && contrasena.chars().anyMatch(Character::isDigit);

        if (correo.isEmpty()) {
            lblErrorCorreo.setText(" ");
        } else if (!correoValido) {
            lblErrorCorreo.setText("Ingresa un correo válido.");
        } else {
            lblErrorCorreo.setText(" ");
        }

        if (contrasena.isEmpty()) {
            lblErrorContrasena.setText(" ");
        } else if (!contrasenaValida) {
            lblErrorContrasena.setText("Mín. 8 caracteres, 1 mayúscula y 1 número.");
        } else {
            lblErrorContrasena.setText(" ");
        }

        lblErrorServidor.setText(" ");
        btnRegistrar.setEnabled(correoValido && contrasenaValida);
    }

    private void registrarUsuario() {
        String correo = txtCorreo.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());

        try {
            casoUsoRegistro.ejecutar(correo, contrasena);

            JOptionPane.showMessageDialog(this, 
                "¡Registro exitoso!", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);

            VistaLogin vistaLogin = new VistaLogin();
            vistaLogin.setVisible(true);
            this.dispose();

        } catch (CorreoInvalidoException | ContrasenaInvalidaException ex) {
            lblErrorServidor.setText(ex.getMessage());
        } catch (CorreoYaRegistradoException ex) {
            lblErrorServidor.setText("El correo ya está registrado.");
        } catch (RegistroFallidoException ex) {
            lblErrorServidor.setText("Error: " + ex.getMessage());
        } catch (Exception ex) {
            lblErrorServidor.setText("Ocurrió un error inesperado.");
        }
    }
}