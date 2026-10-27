package com.orientadorvocacional.repositorio;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Memoria temporal muy sencilla para datos que casi no cambian (preguntas,
 * carreras, universidades, planes de estudio). Evita ir a la base de datos
 * remota en cada peticion. Cada dato vive un tiempo limitado y se puede
 * invalidar a mano cuando alguien lo modifique.
 */
public class CacheTemporal<K, V> {

    private record Entrada<T>(T valor, long venceEn) { }

    /** Todas las memorias creadas, para vaciarlas juntas cuando el administrador cambia datos. */
    private static final Set<CacheTemporal<?, ?>> TODAS = ConcurrentHashMap.newKeySet();

    private final Map<K, Entrada<V>> entradas = new ConcurrentHashMap<>();
    private final long duracionMs;
    private final int maximoEntradas;

    public CacheTemporal(long duracionMs, int maximoEntradas) {
        this.duracionMs = duracionMs;
        this.maximoEntradas = maximoEntradas;
        TODAS.add(this);
    }

    /** Vacia todas las memorias temporales (se usa despues de que el administrador modifica datos). */
    public static void invalidarTodas() {
        for (CacheTemporal<?, ?> cache : TODAS) {
            cache.invalidar();
        }
    }

    /** Devuelve el valor guardado si sigue vigente; si no, lo calcula, lo guarda y lo devuelve. */
    public V obtener(K clave, Supplier<V> cargador) {
        long ahora = System.currentTimeMillis();
        Entrada<V> guardada = entradas.get(clave);
        if (guardada != null && guardada.venceEn() > ahora) {
            return guardada.valor();
        }
        V valor = cargador.get();
        if (entradas.size() >= maximoEntradas) {
            entradas.clear();
        }
        entradas.put(clave, new Entrada<>(valor, ahora + duracionMs));
        return valor;
    }

    /** Consulta sin cargar: devuelve el valor vigente o null. */
    public V consultar(K clave) {
        Entrada<V> guardada = entradas.get(clave);
        return guardada != null && guardada.venceEn() > System.currentTimeMillis() ? guardada.valor() : null;
    }

    public void guardar(K clave, V valor) {
        if (entradas.size() >= maximoEntradas) {
            entradas.clear();
        }
        entradas.put(clave, new Entrada<>(valor, System.currentTimeMillis() + duracionMs));
    }

    public void invalidar() {
        entradas.clear();
    }
}
