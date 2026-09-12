package com.orientadorvocacional.modelo;

/**
 * Representa una opción de respuesta de una pregunta, con el área
 * de interés a la que suma y cuánto "pesa" hacia esa área.
 */
public class OpcionRespuesta {

    private int id;
    private String texto;
    private String area;
    private int peso;

    public OpcionRespuesta(int id, String texto, String area, int peso) {
        setId(id);
        setTexto(texto);
        setArea(area);
        setPeso(peso);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El id de la opción debe ser positivo.");
        }
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("El texto de la opción no puede estar vacío.");
        }
        this.texto = texto;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        if (area == null || area.isBlank()) {
            throw new IllegalArgumentException("El área no puede estar vacía.");
        }
        this.area = area;
    }

    public int getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
        if (peso <= 0) {
            throw new IllegalArgumentException("El peso debe ser un número positivo.");
        }
        this.peso = peso;
    }
}