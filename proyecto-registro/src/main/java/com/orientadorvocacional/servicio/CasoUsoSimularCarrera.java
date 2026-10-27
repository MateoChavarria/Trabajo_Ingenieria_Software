package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.CarreraNoEncontradaException;
import com.orientadorvocacional.excepciones.ConsultaCarrerasFallidaException;
import com.orientadorvocacional.excepciones.ConsultaPensumFallidaException;
import com.orientadorvocacional.excepciones.ConsultaTemasDificilesFallidaException;
import com.orientadorvocacional.excepciones.ConsultaUniversidadesFallidaException;
import com.orientadorvocacional.excepciones.PensumNoDisponibleException;
import com.orientadorvocacional.modelo.MateriaPensum;
import com.orientadorvocacional.modelo.MateriaSimulacion;
import com.orientadorvocacional.modelo.PensumCarrera;
import com.orientadorvocacional.modelo.SemestrePensum;
import com.orientadorvocacional.modelo.SemestreSimulacion;
import com.orientadorvocacional.modelo.SimulacionCarrera;
import com.orientadorvocacional.repositorio.ITemasDificilesRepositorio;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Entrega la estructura completa de una carrera, semestre por semestre
 * (materias, enfoque y temas más difíciles), lista para simularla.
 *
 * No repite la regla "solo UPB Medellín": se apoya en
 * CasoUsoObtenerPensumCarrera, que ya la aplica. Aquí se agrega que la
 * carrera tenga al menos un semestre cargado y los temas difíciles de
 * cada materia.
 */
@Service
public class CasoUsoSimularCarrera {

    private final CasoUsoObtenerPensumCarrera casoUsoPensum;
    private final ITemasDificilesRepositorio repositorioTemas;

    public CasoUsoSimularCarrera(CasoUsoObtenerPensumCarrera casoUsoPensum,
                                 ITemasDificilesRepositorio repositorioTemas) {
        this.casoUsoPensum = casoUsoPensum;
        this.repositorioTemas = repositorioTemas;
    }

    public SimulacionCarrera ejecutar(int carreraId)
            throws CarreraNoEncontradaException, ConsultaCarrerasFallidaException,
                   ConsultaUniversidadesFallidaException, PensumNoDisponibleException,
                   ConsultaPensumFallidaException, ConsultaTemasDificilesFallidaException {

        PensumCarrera pensum = casoUsoPensum.ejecutar(carreraId);

        if (pensum.getSemestres().isEmpty()) {
            throw new PensumNoDisponibleException(
                    "Esta carrera aún no tiene plan de estudios cargado para simular.");
        }

        Map<Integer, Map<String, List<String>>> temasPorSemestre = repositorioTemas.obtenerPorCarrera(carreraId);

        List<SemestreSimulacion> semestres = new ArrayList<>();
        for (SemestrePensum semestre : pensum.getSemestres()) {
            semestres.add(convertirSemestre(semestre, temasPorSemestre.getOrDefault(semestre.getNumero(), Map.of())));
        }
        return new SimulacionCarrera(pensum.getCarreraId(), pensum.getNombreCarrera(), semestres);
    }

    /** Une cada materia del semestre con sus temas difíciles (lista vacía si no tiene o si es electiva). */
    private SemestreSimulacion convertirSemestre(SemestrePensum semestre, Map<String, List<String>> temasPorMateria) {
        List<MateriaSimulacion> materias = new ArrayList<>();
        for (MateriaPensum materia : semestre.getMaterias()) {
            // Las electivas y optativas no llevan temas: dependen de la materia que escoja el estudiante.
            List<String> temas = MateriaSimulacion.esNombreDeMateriaElectiva(materia.getNombre())
                    ? List.of()
                    : temasPorMateria.getOrDefault(materia.getNombre(), List.of());
            materias.add(new MateriaSimulacion(
                    materia.getNombre(),
                    materia.getCreditos(),
                    materia.getEnfoqueTematico(),
                    temas));
        }
        return new SemestreSimulacion(
                semestre.getNumero(), semestre.getEnfoquePrincipal(), semestre.getDescripcion(), materias);
    }
}
