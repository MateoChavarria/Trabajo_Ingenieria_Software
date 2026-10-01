package com.orientadorvocacional.servicio;

import com.orientadorvocacional.modelo.OpcionRespuesta;
import com.orientadorvocacional.modelo.Pregunta;
import com.orientadorvocacional.repositorio.IRepositorioPreguntas;
import com.orientadorvocacional.repositorio.IRepositorioResultadoTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas del calculo de afinidad por area. Se usan mocks de
 * IRepositorioPreguntas e IRepositorioResultadoTest, en vez de
 * conectarse a Supabase de verdad: el cuestionario de prueba se
 * define aqui mismo, a mano, con valores conocidos, para poder
 * verificar que el calculo matematico es correcto.
 */
class CasoUsoCalcularYGuardarResultadoTestTest {

    private IRepositorioPreguntas repositorioPreguntasMock;
    private IRepositorioResultadoTest repositorioResultadoMock;
    private CasoUsoCalcularYGuardarResultadoTest casoUso;

    @BeforeEach
    void configurar() throws Exception {
        repositorioPreguntasMock = mock(IRepositorioPreguntas.class);
        repositorioResultadoMock = mock(IRepositorioResultadoTest.class);
        casoUso = new CasoUsoCalcularYGuardarResultadoTest(repositorioPreguntasMock, repositorioResultadoMock);

        // Cuestionario de prueba: 2 preguntas, cada una con una
        // opcion de "ingenieria" (peso 3) y una de "salud" (peso 3).
        // El maximo posible por area, con estas 2 preguntas, es 6.
        Pregunta pregunta1 = new Pregunta(1, "Pregunta 1", "intereses", List.of(
                new OpcionRespuesta(10, "Opcion ingenieria", "ingenieria", 3),
                new OpcionRespuesta(11, "Opcion salud", "salud", 3)
        ));
        Pregunta pregunta2 = new Pregunta(2, "Pregunta 2", "intereses", List.of(
                new OpcionRespuesta(20, "Opcion ingenieria", "ingenieria", 3),
                new OpcionRespuesta(21, "Opcion salud", "salud", 3)
        ));

        when(repositorioPreguntasMock.obtenerCuestionarioCompleto())
                .thenReturn(List.of(pregunta1, pregunta2));
    }

    /**
     * Dado un usuario que elige la opcion de "ingenieria" en ambas
     * preguntas, Cuando se calcula su resultado, Entonces su afinidad
     * a "ingenieria" debe ser 100% (eligio el maximo posible).
     */
    @Test
    void siElUsuarioEligeSiempreIngenieriaSuAfinidadEsCienPorCiento() throws Exception {
        Map<Integer, Integer> respuestas = Map.of(1, 10, 2, 20);

        Map<String, Integer> resultado = casoUso.ejecutar("usuario-1", respuestas);

        assertThat(resultado.get("ingenieria")).isEqualTo(100);
    }

    /**
     * Dado un usuario que elige una opcion de cada area, Cuando se
     * calcula su resultado, Entonces ambas areas deben quedar con
     * 50% de afinidad (la mitad del maximo posible de 6 puntos).
     */
    @Test
    void siElUsuarioMezclaRespuestasElPuntajeQuedaRepartidoEntreAreas() throws Exception {
        Map<Integer, Integer> respuestas = Map.of(1, 10, 2, 21);

        Map<String, Integer> resultado = casoUso.ejecutar("usuario-2", respuestas);

        assertThat(resultado.get("ingenieria")).isEqualTo(50);
        assertThat(resultado.get("salud")).isEqualTo(50);
    }

    /**
     * El caso de uso debe guardar el resultado calculado a traves
     * del repositorio, exactamente una vez por ejecucion.
     */
    @Test
    void elResultadoCalculadoSeGuardaUnaSolaVez() throws Exception {
        Map<Integer, Integer> respuestas = Map.of(1, 10, 2, 20);

        casoUso.ejecutar("usuario-3", respuestas);

        verify(repositorioResultadoMock, times(1)).guardar(any());
    }
}