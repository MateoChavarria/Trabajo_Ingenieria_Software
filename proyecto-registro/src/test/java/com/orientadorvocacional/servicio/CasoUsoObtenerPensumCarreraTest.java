package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.CarreraNoEncontradaException;
import com.orientadorvocacional.excepciones.PensumNoDisponibleException;
import com.orientadorvocacional.modelo.Carrera;
import com.orientadorvocacional.modelo.PensumCarrera;
import com.orientadorvocacional.modelo.Universidad;
import com.orientadorvocacional.repositorio.ICarreraRepositorio;
import com.orientadorvocacional.repositorio.IPensumRepositorio;
import com.orientadorvocacional.repositorio.IUniversidadRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas de la regla "el pensum solo se entrega si la carrera es de
 * UPB Medellín". Los repositorios son "falsos" escritos a mano aquí
 * mismo, para no conectarse a Supabase.
 */
class CasoUsoObtenerPensumCarreraTest {

    private static final int ID_CARRERA_UPB = 5;
    private static final int ID_CARRERA_DE_OTRA_UNIVERSIDAD = 6;

    private boolean seConsultoElPensum;
    private PensumCarrera pensumDeLaCarreraUpb;
    private CasoUsoObtenerPensumCarrera casoUso;

    @BeforeEach
    void configurar() {
        seConsultoElPensum = false;
        pensumDeLaCarreraUpb = new PensumCarrera(ID_CARRERA_UPB, "Carrera UPB", List.of());

        ICarreraRepositorio carreras = new ICarreraRepositorio() {
            @Override
            public List<Carrera> obtenerTodas() {
                return List.of();
            }

            @Override
            public Carrera obtenerPorId(int id) throws CarreraNoEncontradaException {
                if (id != ID_CARRERA_UPB && id != ID_CARRERA_DE_OTRA_UNIVERSIDAD) {
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
                return carreraId == ID_CARRERA_UPB;
            }
        };

        IPensumRepositorio pensum = carrera -> {
            seConsultoElPensum = true;
            return pensumDeLaCarreraUpb;
        };

        casoUso = new CasoUsoObtenerPensumCarrera(carreras, universidades, pensum);
    }

    /** Dado una carrera de la UPB, cuando se pide el pensum, entonces se entrega. */
    @Test
    void entregaElPensumSiLaCarreraEsDeUpb() throws Exception {
        PensumCarrera resultado = casoUso.ejecutar(ID_CARRERA_UPB);

        assertSame(pensumDeLaCarreraUpb, resultado);
        assertEquals(true, seConsultoElPensum);
    }

    /** Dado una carrera de otra universidad, entonces no se entrega ni se consulta el pensum. */
    @Test
    void noEntregaElPensumSiLaCarreraNoEsDeUpb() {
        assertThrows(PensumNoDisponibleException.class,
                () -> casoUso.ejecutar(ID_CARRERA_DE_OTRA_UNIVERSIDAD));

        assertEquals(false, seConsultoElPensum);
    }

    /** Si la carrera no existe, se propaga CarreraNoEncontradaException. */
    @Test
    void laCarreraInexistenteSePropagaComoNoEncontrada() {
        assertThrows(CarreraNoEncontradaException.class, () -> casoUso.ejecutar(999));

        assertEquals(false, seConsultoElPensum);
    }
}
