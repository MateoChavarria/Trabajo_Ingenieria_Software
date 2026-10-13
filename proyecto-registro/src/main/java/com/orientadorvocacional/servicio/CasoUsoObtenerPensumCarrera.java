package com.orientadorvocacional.servicio;

import com.orientadorvocacional.excepciones.CarreraNoEncontradaException;
import com.orientadorvocacional.excepciones.ConsultaCarrerasFallidaException;
import com.orientadorvocacional.excepciones.ConsultaPensumFallidaException;
import com.orientadorvocacional.excepciones.ConsultaUniversidadesFallidaException;
import com.orientadorvocacional.excepciones.PensumNoDisponibleException;
import com.orientadorvocacional.modelo.Carrera;
import com.orientadorvocacional.modelo.PensumCarrera;
import com.orientadorvocacional.repositorio.ICarreraRepositorio;
import com.orientadorvocacional.repositorio.IPensumRepositorio;
import com.orientadorvocacional.repositorio.IUniversidadRepositorio;
import org.springframework.stereotype.Service;

/**
 * Entrega el plan de estudios de una carrera, pero SOLO si la carrera
 * pertenece a una universidad con información completa (hoy, la UPB
 * Medellín). Para cualquier otra, lanza PensumNoDisponibleException.
 */
@Service
public class CasoUsoObtenerPensumCarrera {

    private final ICarreraRepositorio repositorioCarreras;
    private final IUniversidadRepositorio repositorioUniversidades;
    private final IPensumRepositorio repositorioPensum;

    public CasoUsoObtenerPensumCarrera(ICarreraRepositorio repositorioCarreras,
                                       IUniversidadRepositorio repositorioUniversidades,
                                       IPensumRepositorio repositorioPensum) {
        this.repositorioCarreras = repositorioCarreras;
        this.repositorioUniversidades = repositorioUniversidades;
        this.repositorioPensum = repositorioPensum;
    }

    public PensumCarrera ejecutar(int carreraId)
            throws CarreraNoEncontradaException, ConsultaCarrerasFallidaException,
                   ConsultaUniversidadesFallidaException, PensumNoDisponibleException,
                   ConsultaPensumFallidaException {

        Carrera carrera = repositorioCarreras.obtenerPorId(carreraId);

        if (!repositorioUniversidades.carreraPerteneceAUniversidadConInformacionCompleta(carreraId)) {
            throw new PensumNoDisponibleException(
                    "El plan de estudios solo está disponible para carreras de la UPB Medellín.");
        }
        return repositorioPensum.obtenerPorCarrera(carrera);
    }
}
