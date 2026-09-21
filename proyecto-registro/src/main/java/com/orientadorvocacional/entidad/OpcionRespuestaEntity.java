package com.orientadorvocacional.entidad;

import jakarta.persistence.*;

/**
 * Version "de base de datos" de una opcion de respuesta, mapeada a la
 * tabla "opciones_respuesta". Cada opcion pertenece a una pregunta
 * (relacion @ManyToOne) y define hacia que area suma y cuanto pesa.
 */
@Entity
@Table(name = "opciones_respuesta")
public class OpcionRespuestaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * @JoinColumn indica que la columna real en la tabla se llama
     * "pregunta_id" (asi la creamos con SQL), aunque en Java el campo
     * se llame "pregunta" y sea un objeto completo, no solo un numero.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pregunta_id", nullable = false)
    private PreguntaEntity pregunta;

    @Column(name = "texto", nullable = false)
    private String texto;

    @Column(name = "area", nullable = false)
    private String area;

    @Column(name = "peso", nullable = false)
    private Integer peso;

    protected OpcionRespuestaEntity() {
    }

    public Integer getId() {
        return id;
    }

    public String getTexto() {
        return texto;
    }

    public String getArea() {
        return area;
    }

    public Integer getPeso() {
        return peso;
    }
}
