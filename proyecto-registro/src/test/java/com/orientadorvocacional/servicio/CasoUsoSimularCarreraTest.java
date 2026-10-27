package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.CarreraNoEncontradaException;
import com.orientadorvocacional.excepciones.PensumNoDisponibleException;
import com.orientadorvocacional.modelo.Carrera;
import com.orientadorvocacional.modelo.MateriaPensum;
import com.orientadorvocacional.modelo.MateriaSimulacion;
import com.orientadorvocacional.modelo.PensumCarrera;
import com.orientadorvocacional.modelo.SemestrePensum;
import com.orientadorvocacional.modelo.SimulacionCarrera;
import com.orientadorvocacional.modelo.Universidad;
import com.orientadorvocacional.repositorio.ICarreraRepositorio;
import com.orientadorvocacional.repositorio.IPensumRepositorio;
import com.orientadorvocacional.repositorio.ITemasDificilesRepositorio;
import com.orientadorvocacional.repositorio.IUniversidadRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas de la simulación de carrera. Se apoya en la regla "solo UPB
 * Medellín" del caso de uso del pensum; los repositorios son "falsos"
 * escritos a mano, para no conectarse a Supabase.
 */
class CasoUsoSimularCarreraTest {

    private static final int ID_CARRERA_UPB = 5;
    private static final int ID_CARRERA_UPB_SIN_SEMESTRES = 8;
    private static final int ID_CARRERA_DE_OTRA_UNIVERSIDAD = 6;

    private CasoUsoSimularCarrera casoUso;

    @BeforeEach
    void configurar() {
        ICarreraRepositorio carreras = new ICarreraRepositorio() {
            @Override
            public List<Carrera> obtenerTodas() {
                return List.of();
            }

            @Override
            public Carrera obtenerPorId(int id) throws CarreraNoEncontradaException {
                if (id != ID_CARRERA_UPB && id != ID_CARRERA_UPB_SIN_SEMESTRES
                        && id != ID_CARRERA_DE_OTRA_UNIVERSIDAD) {
                    throw new CarreraNoEncontradaException("No existe una carrera con id " + id + ".");
                }
                return new Carrera(id, "Carrera " + id, "Descripción", "Campo laboral", "ingenieria", 8, null);
            }
        };

        IUniversidadRepositorio universidades = new IUniversidadRepositorio() {
            @Override
            public List<Universidad> obtenerTodas() {
                return List.of();
            }

            @Override
            public boolean carreraPerteneceAUniversidadConInformacionCompleta(int carreraId) {
                return carreraId != ID_CARRERA_DE_OTRA_UNIVERSIDAD;
            }
        };

        // Semestre 1 con dos materias; la segunda no tiene temas registrados.
        IPensumRepositorio pensum = carrera -> {
            if (carrera.getId() == ID_CARRERA_UPB_SIN_SEMESTRES) {
                return new PensumCarrera(carrera.getId(), carrera.getNombre(), List.of());
            }
            SemestrePensum semestre = new SemestrePensum(1, "Fundamentos", "Bases de la carrera", List.of(
                    new MateriaPensum("Cálculo", 4, "Matemáticas"),
                    new MateriaPensum("Ética", 1, "Formación Humanista"),
                    new MateriaPensum("Electiva Universitaria", 2, "Formación Humanista")));
            return new PensumCarrera(carrera.getId(), carrera.getNombre(), List.of(semestre));
        };

        // "Cálculo" tiene temas. La electiva también aparece en la base, pero no debe mostrarse.
        ITemasDificilesRepositorio temas = carreraId ->
                Map.of(1, Map.of("Cálculo", List.of("Límites", "Derivadas"),
                        "Electiva Universitaria", List.of("Tema que no debe salir")));

        casoUso = new CasoUsoSimularCarrera(new CasoUsoObtenerPensumCarrera(carreras, universidades, pensum), temas);
    }

    /** Dado una carrera de la UPB con semestres, se entrega la simulación con sus totales. */
    @Test
    void entregaLaSimulacionSiLaCarreraEsDeUpb() throws Exception {
        SimulacionCarrera simulacion = casoUso.ejecutar(ID_CARRERA_UPB);

        assertEquals(ID_CARRERA_UPB, simulacion.getCarreraId());
        assertEquals(1, simulacion.getTotalSemestres());
        assertEquals(3, simulacion.getTotalMaterias());
        assertEquals(7, simulacion.getCreditosTotales());
        assertEquals("Fundamentos", simulacion.getSemestres().get(0).getEnfoquePrincipal());
    }

    /** Cada materia lleva sus temas difíciles; la que no tiene queda con la lista vacía. */
    @Test
    void cadaMateriaTraeSusTemasDificiles() throws Exception {
        SimulacionCarrera simulacion = casoUso.ejecutar(ID_CARRERA_UPB);

        MateriaSimulacion calculo = simulacion.getSemestres().get(0).getMaterias().get(0);
        MateriaSimulacion etica = simulacion.getSemestres().get(0).getMaterias().get(1);

        assertEquals(List.of("Límites", "Derivadas"), calculo.getTemasDificiles());
        assertEquals(List.of(), etica.getTemasDificiles());
    }

    /** Las electivas y optativas no muestran temas aunque la base los tenga: dependen de la materia que se escoja. */
    @Test
    void laMateriaElectivaNoMuestraTemasDificiles() throws Exception {
        SimulacionCarrera simulacion = casoUso.ejecutar(ID_CARRERA_UPB);

        MateriaSimulacion electiva = simulacion.getSemestres().get(0).getMaterias().get(2);

        assertEquals("Electiva Universitaria", electiva.getNombre());
        assertEquals(true, electiva.isElectiva());
        assertEquals(List.of(), electiva.getTemasDificiles());
    }

    /** Una carrera de otra universidad no se puede simular. */
    @Test
    void noSimulaUnaCarreraQueNoEsDeUpb() {
        assertThrows(PensumNoDisponibleException.class,
                () -> casoUso.ejecutar(ID_CARRERA_DE_OTRA_UNIVERSIDAD));
    }

    /** Una carrera de la UPB sin semestres cargados no se puede simular. */
    @Test
    void noSimulaUnaCarreraSinSemestresCargados() {
        assertThrows(PensumNoDisponibleException.class,
                () -> casoUso.ejecutar(ID_CARRERA_UPB_SIN_SEMESTRES));
    }

    /** Si la carrera no existe, se propaga CarreraNoEncontradaException. */
    @Test
    void laCarreraInexistenteSePropagaComoNoEncontrada() {
        assertThrows(CarreraNoEncontradaException.class, () -> casoUso.ejecutar(999));
    }
}
