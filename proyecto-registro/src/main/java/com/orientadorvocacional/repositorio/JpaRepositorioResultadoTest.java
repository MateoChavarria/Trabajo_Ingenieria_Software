package com.orientadorvocacional.repositorio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orientadorvocacional.entidad.ResultadoTestEntity;
import com.orientadorvocacional.excepciones.ResultadoTestFallidoException;
import com.orientadorvocacional.modelo.ResultadoTest;
import com.orientadorvocacional.repositorio.jpa.ResultadoTestJpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Implementacion de IRepositorioResultadoTest que guarda el resultado
 * directamente en PostgreSQL usando JPA.
 *
 * El mapa area->puntaje (Map<String, Integer>) se convierte a texto
 * JSON antes de guardarlo, porque la columna "detalle" de la tabla es
 * de tipo texto, no una estructura de Java. ObjectMapper (de Jackson,
 * que ya viene incluido con spring-boot-starter-web) hace esa
 * conversion por nosotros.
 */
@Repository
public class JpaRepositorioResultadoTest implements IRepositorioResultadoTest {

    private final ResultadoTestJpaRepository repositorioJpa;
    private final ObjectMapper mapeadorJson;

    public JpaRepositorioResultadoTest(ResultadoTestJpaRepository repositorioJpa) {
        this.repositorioJpa = repositorioJpa;
        this.mapeadorJson = new ObjectMapper();
    }

    @Override
    public void guardar(ResultadoTest resultado) throws ResultadoTestFallidoException {
        try {
            String detalleJson = mapeadorJson.writeValueAsString(resultado.getDetallePorArea());

                        ResultadoTestEntity entidad = new ResultadoTestEntity(
                    java.util.UUID.fromString(resultado.getUsuarioId()),
                    resultado.getFecha(),
                    detalleJson
            );

            repositorioJpa.save(entidad);

        } catch (IllegalArgumentException excepcion) {
            throw new ResultadoTestFallidoException(
                    "El id del usuario no tiene un formato UUID válido.", excepcion);
        } catch (JsonProcessingException excepcion) {
            throw new ResultadoTestFallidoException(
                    "No se pudo convertir el detalle del resultado a formato JSON.", excepcion);

        } catch (Exception excepcion) {
            throw new ResultadoTestFallidoException(
                    "No se pudo guardar el resultado del test en la base de datos.", excepcion);
        }
    }
}
