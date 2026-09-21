package com.orientadorvocacional.entidad;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Version "de base de datos" de una pregunta del test vocacional,
 * mapeada a la tabla "preguntas".
 *
 * La relacion @OneToMany trae automaticamente, en una sola consulta
 * (gracias a FetchType.EAGER), todas las opciones de respuesta que le
 * pertenecen a esta pregunta — exactamente lo mismo que antes
 * lograbamos con el "embedding" de PostgREST
 * (select=...,opciones_respuesta(...)).
 */
@Entity
@Table(name = "preguntas")
public class PreguntaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "texto", nullable = false)
    private String texto;

    @Column(name = "categoria", nullable = false)
    private String categoria;

    /**
     * "mappedBy = pregunta" indica que la relacion la controla el
     * campo "pregunta" de OpcionRespuestaEntity (la llave foranea
     * pregunta_id vive en esa tabla, no en esta).
     *
     * FetchType.EAGER es una simplificacion deliberada: como el test
     * tiene pocas preguntas (unas 15), no hay problema en traerlas
     * todas de una vez. En un sistema con miles de preguntas, se
     * preferiria FetchType.LAZY y cargarlas bajo demanda.
     */
    @OneToMany(mappedBy = "pregunta", fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    private List<OpcionRespuestaEntity> opciones = new ArrayList<>();

    protected PreguntaEntity() {
    }

    public Integer getId() {
        return id;
    }

    public String getTexto() {
        return texto;
    }

    public String getCategoria() {
        return categoria;
    }

    public List<OpcionRespuestaEntity> getOpciones() {
        return opciones;
    }
}
