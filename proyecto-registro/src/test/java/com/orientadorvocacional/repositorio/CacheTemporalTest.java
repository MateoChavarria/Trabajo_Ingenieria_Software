package com.orientadorvocacional.repositorio;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class CacheTemporalTest {

    @Test
    void segundaConsultaNoVuelveACargar() {
        CacheTemporal<String, Integer> cache = new CacheTemporal<>(60_000, 10);
        AtomicInteger cargas = new AtomicInteger();

        cache.obtener("a", cargas::incrementAndGet);
        int segundo = cache.obtener("a", cargas::incrementAndGet);

        assertThat(cargas.get()).isEqualTo(1);
        assertThat(segundo).isEqualTo(1);
    }

    @Test
    void datoVencidoSeVuelveACargar() throws Exception {
        CacheTemporal<String, Integer> cache = new CacheTemporal<>(20, 10);
        AtomicInteger cargas = new AtomicInteger();

        cache.obtener("a", cargas::incrementAndGet);
        Thread.sleep(60);
        cache.obtener("a", cargas::incrementAndGet);

        assertThat(cargas.get()).isEqualTo(2);
    }

    @Test
    void invalidarObligaARecargar() {
        CacheTemporal<String, Integer> cache = new CacheTemporal<>(60_000, 10);
        AtomicInteger cargas = new AtomicInteger();

        cache.obtener("a", cargas::incrementAndGet);
        cache.invalidar();
        cache.obtener("a", cargas::incrementAndGet);

        assertThat(cargas.get()).isEqualTo(2);
    }

    @Test
    void consultarSinDatoDevuelveNull() {
        assertThat(new CacheTemporal<String, Integer>(60_000, 10).consultar("x")).isNull();
    }
}
