package com.orientadorvocacional.modelo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Datos que viajan entre el panel de administracion y el servidor.
 * Son registros sencillos (sin logica): las reglas viven en ServicioAdmin.
 */
public final class DatosAdmin {

    private DatosAdmin() {
    }

    public record Resumen(long usuarios, long usuariosConAcceso, long pagosExitosos, long ingresosTotales,
                          long testsRealizados, long carreras, long universidades, long preguntas) {
    }

    public record Usuario(String id, String correo, String fechaRegistro, String estadoCuenta,
                          boolean accesoCompleto) {
    }

    public record Pago(long id, String correo, int monto, String estado, String referencia,
                       String fecha, String motivoFallo) {
    }

    public record Carrera(Integer id, String nombre, String descripcion, String campoLaboral,
                          String areaCategoria, Integer duracionSemestres, BigDecimal tarifaSemestre,
                          Integer universidadId) {
    }

    public record Universidad(Integer id, String nombre, String caracter, String municipio,
                              boolean tieneInformacionCompleta) {
    }

    public record Opcion(int id, String texto, String area, int peso) {
    }

    public record Pregunta(int id, String texto, String categoria, List<Opcion> opciones) {
    }

    /** Cuerpo para cambiar solo el texto de una pregunta o de una opcion. */
    public record TextoNuevo(String texto) {
    }
}
