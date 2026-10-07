package com.orientadorvocacional.repositorio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orientadorvocacional.entidad.ResultadoTestEntity;
import com.orientadorvocacional.excepciones.ResultadoTestFallidoException;
import com.orientadorvocacional.modelo.ResultadoTest;
import com.orientadorvocacional.repositorio.jpa.ResultadoTestJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                    UUID.fromString(resultado.getUsuarioId()),
                    resultado.getFecha(),
                    detalleJson
            );

            repositorioJpa.save(entidad);

        } catch (IllegalArgumentException excepcion) {
            throw new ResultadoTestFallidoException("El id del usuario no tiene un formato UUID válido.", excepcion);
        } catch (JsonProcessingException excepcion) {
            throw new ResultadoTestFallidoException("No se pudo convertir el detalle del resultado a JSON.", excepcion);
        } catch (Exception excepcion) {
            throw new ResultadoTestFallidoException("No se pudo guardar el resultado del test.", excepcion);
        }
    }

    @Override
    public List<ResultadoTest> obtenerPorUsuario(String usuarioId) throws ResultadoTestFallidoException {
        try {
            List<ResultadoTestEntity> entidades =
                    repositorioJpa.findByUsuarioIdOrderByFechaDesc(UUID.fromString(usuarioId));

            List<ResultadoTest> resultados = new ArrayList<>();
            for (ResultadoTestEntity entidad : entidades) {
                Map<String, Integer> detalle = mapeadorJson.readValue(
                        entidad.getDetalle(), new TypeReference<Map<String, Integer>>() {});

                ResultadoTest resultado = new ResultadoTest(usuarioId, detalle);
                resultado.setFecha(entidad.getFecha());
                resultados.add(resultado);
            }
            return resultados;

        } catch (Exception excepcion) {
            throw new ResultadoTestFallidoException("No se pudo consultar el historial de resultados.", excepcion);
        }
    }
}