package com.orientadorvocacional.modelo;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Pruebas de la simulación de carrera y de sus semestres y materias. */
class SimulacionCarreraTest {

    private static SimulacionCarrera simulacionDeEjemplo() {
        SemestreSimulacion primero = new SemestreSimulacion(1, "Fundamentos", "Bases de la carrera", List.of(
                new MateriaSimulacion("Cálculo", 4, "Matemáticas", List.of("Límites", "Derivadas")),
                new MateriaSimulacion("Programación", 3, "Programación", List.of("Recursión", "Punteros"))));
        SemestreSimulacion segundo = new SemestreSimulacion(2, "Estructuras", "Datos y algoritmos", List.of(
                new MateriaSimulacion("Estructuras de datos", 4, "Programación", List.of())));
        return new SimulacionCarrera(7, "Ingeniería de Software", List.of(primero, segundo));
    }

    /** La duración, las materias y los créditos se calculan a partir de los semestres. */
    @Test
    void calculaLaDuracionLasMateriasYLosCreditosTotales() {
        SimulacionCarrera simulacion = simulacionDeEjemplo();

        assertEquals(2, simulacion.getTotalSemestres());
        assertEquals(3, simulacion.getTotalMaterias());
        assertEquals(11, simulacion.getCreditosTotales());
    }

    /** Se conservan el id, el nombre y el orden de los semestres. */
    @Test
    void conservaLosDatosDeLaCarreraYElOrdenDeLosSemestres() {
        SimulacionCarrera simulacion = simulacionDeEjemplo();

        assertEquals(7, simulacion.getCarreraId());
        assertEquals("Ingeniería de Software", simulacion.getNombreCarrera());
        assertEquals(1, simulacion.getSemestres().get(0).getNumero());
        assertEquals(2, simulacion.getSemestres().get(1).getNumero());
    }

    /** Siempre viaja el aviso de que es una simulación referencial. */
    @Test
    void incluyeElAvisoDeSimulacionReferencial() {
        SimulacionCarrera simulacion = simulacionDeEjemplo();

        assertFalse(simulacion.getAviso().isBlank());
        assertEquals(SimulacionCarrera.AVISO, simulacion.getAviso());
    }

    /** Sin semestres no hay nada que simular; tampoco se aceptan id ni nombre inválidos. */
    @Test
    void rechazaSemestresVaciosOIdYNombreInvalidos() {
        SemestreSimulacion semestre = new SemestreSimulacion(1, "Enfoque", "Descripción", List.of());

        assertThrows(IllegalArgumentException.class, () -> new SimulacionCarrera(7, "Carrera", null));
        assertThrows(IllegalArgumentException.class, () -> new SimulacionCarrera(7, "Carrera", List.of()));
        assertThrows(IllegalArgumentException.class, () -> new SimulacionCarrera(0, "Carrera", List.of(semestre)));
        assertThrows(IllegalArgumentException.class, () -> new SimulacionCarrera(7, " ", List.of(semestre)));
    }

    /** Una materia guarda sus temas en orden y sin permitir que se modifiquen desde fuera. */
    @Test
    void laMateriaGuardaSusTemasEnOrdenYDeSoloLectura() {
        MateriaSimulacion materia = new MateriaSimulacion("Cálculo", 4, "Matemáticas", List.of("Límites", "Derivadas"));

        assertEquals(List.of("Límites", "Derivadas"), materia.getTemasDificiles());
        assertThrows(UnsupportedOperationException.class, () -> materia.getTemasDificiles().add("Otro"));
    }

    /** Se aceptan hasta 3 temas, y una lista vacía; más de 3, nulos o vacíos se rechazan. */
    @Test
    void laMateriaRechazaMasDeTresTemasOTemasInvalidos() {
        assertEquals(3, new MateriaSimulacion("M", 1, "E", List.of("a", "b", "c")).getTemasDificiles().size());
        assertEquals(0, new MateriaSimulacion("M", 1, "E", List.of()).getTemasDificiles().size());

        assertThrows(IllegalArgumentException.class,
                () -> new MateriaSimulacion("M", 1, "E", List.of("a", "b", "c", "d")));
        assertThrows(IllegalArgumentException.class, () -> new MateriaSimulacion("M", 1, "E", null));
        assertThrows(IllegalArgumentException.class, () -> new MateriaSimulacion("M", 1, "E", List.of(" ")));
    }

    /** Se reconocen las electivas y optativas por el nombre, sin importar tildes ni mayúsculas. */
    @Test
    void reconoceLasMateriasElectivasYOptativas() {
        assertEquals(true, MateriaSimulacion.esNombreDeMateriaElectiva("Electiva Universitaria"));
        assertEquals(true, MateriaSimulacion.esNombreDeMateriaElectiva("OPTATIVA CBD"));
        assertEquals(true, MateriaSimulacion.esNombreDeMateriaElectiva("Seminario Optativo I"));
        assertEquals(true, MateriaSimulacion.esNombreDeMateriaElectiva("Curso Electivo CBFH 1"));
        assertEquals(false, MateriaSimulacion.esNombreDeMateriaElectiva("Cálculo Diferencial"));
        assertEquals(false, MateriaSimulacion.esNombreDeMateriaElectiva(null));
        assertEquals(true, new MateriaSimulacion("Electiva I", 2, "E", List.of()).isElectiva());
    }

    /** Una materia electiva u optativa no puede tener temas difíciles propios. */
    @Test
    void laMateriaElectivaRechazaTemasDificiles() {
        assertThrows(IllegalArgumentException.class,
                () -> new MateriaSimulacion("Optativa I", 2, "E", List.of("Un tema")));
        assertEquals(0, new MateriaSimulacion("Optativa I", 2, "E", List.of()).getTemasDificiles().size());
    }

    /** La materia rechaza nombre o enfoque vacíos y créditos negativos; el semestre, número no positivo. */
    @Test
    void validaLosDatosBasicosDeLaMateriaYElSemestre() {
        assertThrows(IllegalArgumentException.class, () -> new MateriaSimulacion("", 1, "E", List.of()));
        assertThrows(IllegalArgumentException.class, () -> new MateriaSimulacion("M", 1, " ", List.of()));
        assertThrows(IllegalArgumentException.class, () -> new MateriaSimulacion("M", -1, "E", List.of()));
        assertEquals(0, new MateriaSimulacion("M", 0, "E", List.of()).getCreditos());

        assertThrows(IllegalArgumentException.class, () -> new SemestreSimulacion(0, "E", "D", List.of()));
        assertThrows(IllegalArgumentException.class, () -> new SemestreSimulacion(1, "E", "D", null));
    }
}
