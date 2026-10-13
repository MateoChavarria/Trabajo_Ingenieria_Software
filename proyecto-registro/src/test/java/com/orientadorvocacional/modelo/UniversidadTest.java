package com.orientadorvocacional.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de las reglas de validación de Universidad. El constructor
 * delega en los setters, así que probar el constructor prueba los setters.
 */
class UniversidadTest {

    @Test
    void creaUnaUniversidadValida() {
        Universidad universidad = new Universidad(
                1, "Universidad Pontificia Bolivariana (UPB)", "Privada", "Medellín", true);

        assertEquals(1, universidad.getId());
        assertEquals("Privada", universidad.getCaracter());
        assertEquals("Medellín", universidad.getMunicipio());
        assertTrue(universidad.isTieneInformacionCompleta());
    }

    @Test
    void laInformacionCompletaPuedeSerFalsa() {
        Universidad universidad = new Universidad(2, "Universidad EAFIT", "Privada", "Medellín, Rionegro", false);

        assertFalse(universidad.isTieneInformacionCompleta());
    }

    @Test
    void rechazaUnIdNoPositivo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Universidad(0, "UPB", "Privada", "Medellín", true));
    }

    @Test
    void rechazaUnNombreVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Universidad(1, "  ", "Privada", "Medellín", true));
    }

    @Test
    void rechazaUnCaracterNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Universidad(1, "UPB", null, "Medellín", true));
    }

    @Test
    void rechazaUnMunicipioVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Universidad(1, "UPB", "Privada", "", true));
    }
}
