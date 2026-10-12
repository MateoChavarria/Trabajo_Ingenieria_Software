package com.orientadorvocacional.modelo;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas del plan de estudios: validaciones de cada clase y suma de
 * créditos por semestre y por carrera.
 */
class PensumCarreraTest {

    private SemestrePensum semestreDeEjemplo(int numero, int creditosA, int creditosB) {
        return new SemestrePensum(numero, "Fundamentación", "Bases de la carrera", List.of(
                new MateriaPensum("Materia A", creditosA, "Técnico / Aplicado"),
                new MateriaPensum("Materia B", creditosB, "Formación Humanista")
        ));
    }

    @Test
    void sumaLosCreditosDeUnSemestre() {
        assertEquals(6, semestreDeEjemplo(1, 4, 2).getCreditosTotales());
    }

    @Test
    void sumaLosCreditosDeTodaLaCarrera() {
        PensumCarrera pensum = new PensumCarrera(1, "Ingeniería de Sistemas e Informática",
                List.of(semestreDeEjemplo(1, 4, 2), semestreDeEjemplo(2, 3, 3)));

        assertEquals(12, pensum.getCreditosTotales());
    }

    @Test
    void unSemestreSinMateriasSumaCero() {
        SemestrePensum vacio = new SemestrePensum(1, "Fundamentación", "Bases", List.of());

        assertEquals(0, vacio.getCreditosTotales());
    }

    @Test
    void laListaDeMateriasEsDeSoloLectura() {
        List<MateriaPensum> original = new ArrayList<>();
        original.add(new MateriaPensum("Materia A", 2, "Técnico / Aplicado"));
        SemestrePensum semestre = new SemestrePensum(1, "Fundamentación", "Bases", original);

        original.add(new MateriaPensum("Materia B", 2, "Técnico / Aplicado"));

        assertEquals(1, semestre.getMaterias().size());
        assertThrows(UnsupportedOperationException.class,
                () -> semestre.getMaterias().add(new MateriaPensum("Otra", 1, "Técnico / Aplicado")));
    }

    @Test
    void laMateriaAceptaCeroCreditosPeroNoNegativos() {
        assertEquals(0, new MateriaPensum("Actividad sin créditos", 0, "Formación Humanista").getCreditos());
        assertThrows(IllegalArgumentException.class,
                () -> new MateriaPensum("Materia", -1, "Formación Humanista"));
    }

    @Test
    void laMateriaRechazaNombreOEnfoqueVacios() {
        assertThrows(IllegalArgumentException.class, () -> new MateriaPensum(" ", 2, "Técnico / Aplicado"));
        assertThrows(IllegalArgumentException.class, () -> new MateriaPensum("Materia", 2, null));
    }

    @Test
    void elSemestreRechazaNumeroNoPositivoYMateriasNulas() {
        assertThrows(IllegalArgumentException.class,
                () -> new SemestrePensum(0, "Fundamentación", "Bases", List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new SemestrePensum(1, "Fundamentación", "Bases", null));
    }

    @Test
    void elPensumRechazaIdNoPositivoNombreVacioYSemestresNulos() {
        assertThrows(IllegalArgumentException.class, () -> new PensumCarrera(0, "Carrera", List.of()));
        assertThrows(IllegalArgumentException.class, () -> new PensumCarrera(1, "", List.of()));
        assertThrows(IllegalArgumentException.class, () -> new PensumCarrera(1, "Carrera", null));
    }
}
