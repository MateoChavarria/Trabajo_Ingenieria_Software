package com.orientadorvocacional.servicio;

import com.orientadorvocacional.modelo.DatosTarjeta;
import com.orientadorvocacional.modelo.ResultadoCobro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de la pasarela simulada: el resultado depende solo del numero de tarjeta de prueba. */
class PasarelaSimuladaTest {

    private PasarelaSimulada pasarela;

    @BeforeEach
    void configurar() {
        pasarela = new PasarelaSimulada();
    }

    private DatosTarjeta tarjeta(String numero) {
        YearMonth futuro = YearMonth.now().plusYears(2);
        String vencimiento = String.format("%02d/%02d", futuro.getMonthValue(), futuro.getYear() % 100);
        return new DatosTarjeta("Ana Perez", numero, vencimiento, "123");
    }

    @Test
    void laTarjetaDePruebaAprobadaCobraCorrectamente() {
        ResultadoCobro resultado = pasarela.cobrar(tarjeta("4242 4242 4242 4242"), 20000, "PAY-0123456789AB");

        assertTrue(resultado.isAprobado());
        assertNull(resultado.getMotivo());
    }

    @Test
    void laTarjetaSinFondosEsRechazada() {
        ResultadoCobro resultado = pasarela.cobrar(tarjeta("4000000000000002"), 20000, "PAY-0123456789AB");

        assertFalse(resultado.isAprobado());
        assertEquals("Fondos insuficientes.", resultado.getMotivo());
    }

    @Test
    void laTarjetaRechazadaPorElBancoEsRechazada() {
        ResultadoCobro resultado = pasarela.cobrar(tarjeta("4000000000000069"), 20000, "PAY-0123456789AB");

        assertFalse(resultado.isAprobado());
        assertEquals("El banco emisor rechazó la tarjeta.", resultado.getMotivo());
    }

    @Test
    void cualquierOtraTarjetaEsRechazadaPorNoSerDePrueba() {
        ResultadoCobro resultado = pasarela.cobrar(tarjeta("5555555555554444"), 20000, "PAY-0123456789AB");

        assertFalse(resultado.isAprobado());
        assertTrue(resultado.getMotivo().contains("no es una tarjeta de prueba"));
    }
}
