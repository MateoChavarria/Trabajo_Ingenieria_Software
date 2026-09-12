 package com.orientadorvocacional.modelo;

import java.util.List;

/**
 * Representa una pregunta del test vocacional, con sus opciones
 * de respuesta ya asociadas.
 */
public class Pregunta {

    private int id;
    private String texto;
    private String categoria;
    private List<OpcionRespuesta> opciones;

    public Pregunta(int id, String texto, String categoria, List<OpcionRespuesta> opciones) {
        setId(id);
        setTexto(texto);
        setCategoria(categoria);
        setOpciones(opciones);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El id de la pregunta debe ser positivo.");
        }
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("El texto de la pregunta no puede estar vacío.");
        }
        this.texto = texto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            throw new IllegalArgumentException("La categoría no puede estar vacía.");
        }
        this.categoria = categoria;
    }

    public List<OpcionRespuesta> getOpciones() {
        return opciones;
    }

    public void setOpciones(List<OpcionRespuesta> opciones) {
        if (opciones == null || opciones.isEmpty()) {
            throw new IllegalArgumentException("La pregunta debe tener al menos una opción.");
        }
        this.opciones = opciones;
    }
}