package com.orientadorvocacional.repositorio;

import com.orientadorvocacional.entidad.OpcionRespuestaEntity;
import com.orientadorvocacional.entidad.PreguntaEntity;
import com.orientadorvocacional.excepciones.CuestionarioFallidoException;
import com.orientadorvocacional.modelo.OpcionRespuesta;
import com.orientadorvocacional.modelo.Pregunta;
import com.orientadorvocacional.repositorio.jpa.PreguntaJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion de IRepositorioPreguntas que trae las preguntas y
 * sus opciones directamente de PostgreSQL usando JPA, en vez de
 * llamar a la API REST de Supabase (PostgREST) como hacia la version
 * anterior (SupabaseRepositorioPreguntas, ya eliminada).
 */
@Repository
public class JpaRepositorioPreguntas implements IRepositorioPreguntas {

    private final PreguntaJpaRepository repositorioJpa;

    public JpaRepositorioPreguntas(PreguntaJpaRepository repositorioJpa) {
        this.repositorioJpa = repositorioJpa;
    }

    @Override
    public List<Pregunta> obtenerCuestionarioCompleto() throws CuestionarioFallidoException {
        try {
            List<PreguntaEntity> entidades = repositorioJpa.findAllConOpcionesOrderByIdAsc();
            return convertirAModeloDeDominio(entidades);

        } catch (Exception excepcion) {
            throw new CuestionarioFallidoException(
                    "No se pudo obtener el cuestionario desde la base de datos.", excepcion);
        }
    }

    /**
     * Convierte la lista de entidades JPA (PreguntaEntity/
     * OpcionRespuestaEntity) al modelo de dominio (Pregunta/
     * OpcionRespuesta) que ya usa el resto del sistema, incluyendo la
     * validacion que ya tenian esas clases desde que las creamos.
     */
    private List<Pregunta> convertirAModeloDeDominio(List<PreguntaEntity> entidades) {
        List<Pregunta> preguntas = new ArrayList<>();

        for (PreguntaEntity entidadPregunta : entidades) {
            List<OpcionRespuesta> opciones = new ArrayList<>();

            for (OpcionRespuestaEntity entidadOpcion : entidadPregunta.getOpciones()) {
                opciones.add(new OpcionRespuesta(
                        entidadOpcion.getId(),
                        entidadOpcion.getTexto(),
                        entidadOpcion.getArea(),
                        entidadOpcion.getPeso()
                ));
            }

            preguntas.add(new Pregunta(
                    entidadPregunta.getId(),
                    entidadPregunta.getTexto(),
                    entidadPregunta.getCategoria(),
                    opciones
            ));
        }

        return preguntas;
    }
}
