package com.orientadorvocacional.vista;

import com.orientadorvocacional.excepciones.CuestionarioFallidoException;
import com.orientadorvocacional.modelo.OpcionRespuesta;
import com.orientadorvocacional.modelo.Pregunta;
import com.orientadorvocacional.modelo.SesionUsuario;
import com.orientadorvocacional.servicio.CasoUsoObtenerCuestionario;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VistaTestVocacional extends JFrame {

    private final CasoUsoObtenerCuestionario casoUsoTest;
    @SuppressWarnings("unused")
    private final SesionUsuario sesion;

    private List<Pregunta> preguntas;
    private int indiceActual = 0;

    // Guarda temporalmente, en memoria, la opción elegida por cada
    // pregunta (id de pregunta -> id de opción elegida), tal como
    // pide la tarea técnica de "guardar temporalmente las respuestas".
    private final Map<Integer, Integer> respuestasSeleccionadas = new HashMap<>();

    private JLabel lblPregunta;
    private JPanel panelOpciones;
    private ButtonGroup grupoOpciones;
    private JButton btnSiguiente;
    private JLabel lblAviso;

    public VistaTestVocacional(CasoUsoObtenerCuestionario casoUsoTest, SesionUsuario sesion) {
        this.casoUsoTest = casoUsoTest;
        this.sesion = sesion;

        setTitle("Test Vocacional");
        setSize(480, 340);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponentes();
        cargarCuestionario();
    }

    private void initComponentes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblPregunta = new JLabel(" ", SwingConstants.CENTER);
        lblPregunta.setFont(new Font("SansSerif", Font.BOLD, 15));
        panel.add(lblPregunta, BorderLayout.NORTH);

        panelOpciones = new JPanel();
        panelOpciones.setLayout(new BoxLayout(panelOpciones, BoxLayout.Y_AXIS));
        panel.add(panelOpciones, BorderLayout.CENTER);

        lblAviso = new JLabel(" ", SwingConstants.CENTER);
        lblAviso.setForeground(Color.RED);

        btnSiguiente = new JButton("Siguiente");
        btnSiguiente.addActionListener(e -> irASiguiente());

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(lblAviso, BorderLayout.NORTH);
        panelInferior.add(btnSiguiente, BorderLayout.SOUTH);
        panel.add(panelInferior, BorderLayout.SOUTH);

        add(panel);
    }

    private void cargarCuestionario() {
        try {
            preguntas = casoUsoTest.ejecutar();

            if (preguntas.isEmpty()) {
                lblPregunta.setText("No hay preguntas configuradas todavía.");
                btnSiguiente.setEnabled(false);
                return;
            }

            mostrarPregunta(indiceActual);

        } catch (CuestionarioFallidoException ex) {
            lblPregunta.setText("Error al cargar el test: " + ex.getMessage());
            btnSiguiente.setEnabled(false);
        }
    }

    private void mostrarPregunta(int indice) {
        Pregunta pregunta = preguntas.get(indice);

        lblPregunta.setText("<html><div style='text-align:center;'>"
                + (indice + 1) + "/" + preguntas.size() + " — " + pregunta.getTexto()
                + "</div></html>");

        panelOpciones.removeAll();
        grupoOpciones = new ButtonGroup();

        Integer opcionYaElegida = respuestasSeleccionadas.get(pregunta.getId());

        for (OpcionRespuesta opcion : pregunta.getOpciones()) {
            JRadioButton radio = new JRadioButton(opcion.getTexto());
            radio.putClientProperty("idOpcion", opcion.getId());

            if (opcionYaElegida != null && opcionYaElegida == opcion.getId()) {
                radio.setSelected(true);
            }

            grupoOpciones.add(radio);
            panelOpciones.add(radio);
        }

        lblAviso.setText(" ");
        btnSiguiente.setText(indice == preguntas.size() - 1 ? "Finalizar" : "Siguiente");

        panelOpciones.revalidate();
        panelOpciones.repaint();
    }

    private void irASiguiente() {
        Pregunta preguntaActual = preguntas.get(indiceActual);

        Integer idOpcionElegida = obtenerOpcionSeleccionada();

        // No dejar avanzar si no respondió la pregunta obligatoria.
        if (idOpcionElegida == null) {
            lblAviso.setText("Debes seleccionar una opción para continuar.");
            return;
        }

        // Guardar temporalmente la respuesta marcada.
        respuestasSeleccionadas.put(preguntaActual.getId(), idOpcionElegida);

        if (indiceActual == preguntas.size() - 1) {
            // Última pregunta: por ahora solo confirmamos que se guardaron
            // todas las respuestas. El cálculo de afinidad y el listado de
            // carreras recomendadas se hará en un sprint posterior (H3
            // parte 2 y H5).
            JOptionPane.showMessageDialog(this,
                    "¡Test completado! Respuestas registradas: " + respuestasSeleccionadas.size(),
                    "Test finalizado",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        indiceActual++;
        mostrarPregunta(indiceActual);
    }

    private Integer obtenerOpcionSeleccionada() {
        for (Component componente : panelOpciones.getComponents()) {
            if (componente instanceof JRadioButton radio && radio.isSelected()) {
                return (Integer) radio.getClientProperty("idOpcion");
            }
        }
        return null;
    }
}