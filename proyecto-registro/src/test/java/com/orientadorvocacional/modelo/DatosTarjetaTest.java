package com.orientadorvocacional.modelo;

import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de la validacion de los datos de tarjeta de la pasarela simulada. */
class DatosTarjetaTest {

    /** Un vencimiento que siempre esta en el futuro, sin depender de la fecha en que se corra la prueba. */
    private static String vencimientoFuturo() {
        YearMonth futuro = YearMonth.now().plusYears(2);
        return String.format("%02d/%02d", futuro.getMonthValue(), futuro.getYear() % 100);
    }

    @Test
    void aceptaDatosValidosYLimpiaElNumero() {
        DatosTarjeta tarjeta = new DatosTarjeta(" Ana Perez ", "4242 4242-4242 4242", vencimientoFuturo(), "123");

        assertEquals("Ana Perez", tarjeta.getNombreTitular());
        assertEquals("4242424242424242", tarjeta.getNumeroTarjeta());
        assertEquals("123", tarjeta.getCvv());
    }

    @Test
    void rechazaNombreVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new DatosTarjeta("  ", "4242424242424242", vencimientoFuturo(), "123"));
    }

    @Test
    void rechazaNumerosConLetrasOMuyCortos() {
        assertThrows(IllegalArgumentException.class,
                () -> new DatosTarjeta("Ana", "4242abcd42424242", vencimientoFuturo(), "123"));
        assertThrows(IllegalArgumentException.class,
                () -> new DatosTarjeta("Ana", "4242", vencimientoFuturo(), "123"));
        assertThrows(IllegalArgumentException.class,
                () -> new DatosTarjeta("Ana", null, vencimientoFuturo(), "123"));
    }

    @Test
    void rechazaVencimientoConFormatoIncorrecto() {
        assertThrows(IllegalArgumentException.class,
                () -> new DatosTarjeta("Ana", "4242424242424242", "13/30", "123"));
        assertThrows(IllegalArgumentException.class,
                () -> new DatosTarjeta("Ana", "4242424242424242", "1/30", "123"));
        assertThrows(IllegalArgumentException.class,
                () -> new DatosTarjeta("Ana", "4242424242424242", "2030-01", "123"));
    }

    @Test
    void rechazaTarjetaVencida() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new DatosTarjeta("Ana", "4242424242424242", "01/20", "123"));
        assertEquals("La tarjeta está vencida.", error.getMessage());
    }

    @Test
    void rechazaCvvInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> new DatosTarjeta("Ana", "4242424242424242", vencimientoFuturo(), "12"));
        assertThrows(IllegalArgumentException.class,
                () -> new DatosTarjeta("Ana", "4242424242424242", vencimientoFuturo(), "12a"));
    }

    @Test
    void elTextoDelObjetoNoMuestraDatosDeLaTarjeta() {
        DatosTarjeta tarjeta = new DatosTarjeta("Ana", "4242424242424242", vencimientoFuturo(), "123");

        assertFalse(tarjeta.toString().contains("4242"));
        assertFalse(tarjeta.toString().contains("123"));
    }
}
