package com.orientadorvocacional.modelo;

/**
 * Representa una universidad o institución de educación superior de
 * Antioquia.
 *
 * Mismo patrón de encapsulamiento del resto del proyecto: el
 * constructor NO valida por sí mismo, delega en los setters, y los
 * setters son los que validan.
 *
 * "tieneInformacionCompleta" indica si la aplicación tiene las carreras
 * y el plan de estudios de esa universidad (hoy, solo la UPB).
 */
public class Universidad {

    private int id;
    private String nombre;
    private String caracter;
    private String municipio;
    private boolean tieneInformacionCompleta;

    public Universidad(int id, String nombre, String caracter, String municipio,
                       boolean tieneInformacionCompleta) {
        setId(id);
        setNombre(nombre);
        setCaracter(caracter);
        setMunicipio(municipio);
        setTieneInformacionCompleta(tieneInformacionCompleta);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El id de la universidad debe ser positivo.");
        }
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la universidad no puede estar vacío.");
        }
        this.nombre = nombre;
    }

    /** "Pública" o "Privada". */
    public String getCaracter() {
        return caracter;
    }

    public void setCaracter(String caracter) {
        if (caracter == null || caracter.isBlank()) {
            throw new IllegalArgumentException("El carácter de la universidad no puede estar vacío.");
        }
        this.caracter = caracter;
    }

    /** Municipio o municipios donde tiene sede (texto tal como viene de la fuente). */
    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        if (municipio == null || municipio.isBlank()) {
            throw new IllegalArgumentException("El municipio de la universidad no puede estar vacío.");
        }
        this.municipio = municipio;
    }

    public boolean isTieneInformacionCompleta() {
        return tieneInformacionCompleta;
    }

    public void setTieneInformacionCompleta(boolean tieneInformacionCompleta) {
        this.tieneInformacionCompleta = tieneInformacionCompleta;
    }
}
