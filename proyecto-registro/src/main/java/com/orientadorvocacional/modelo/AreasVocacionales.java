package com.orientadorvocacional.modelo;

import java.util.Map;

public final class AreasVocacionales {

    private AreasVocacionales() {
    }

    private static final Map<String, String> NOMBRES_AMIGABLES = Map.ofEntries(
            Map.entry("ingenieria", "Ingenierías"),
            Map.entry("administracion", "Economía, Administración y Negocios"),
            Map.entry("diseño", "Arquitectura y Diseño"),
            Map.entry("ciencias_sociales", "Ciencias Sociales"),
            Map.entry("educacion", "Educación y Pedagogía"),
            Map.entry("humanidades", "Teología, Filosofía y Humanidades"),
            Map.entry("derecho", "Derecho y Ciencias Políticas"),
            Map.entry("salud", "Ciencias de la Salud")
    );

    /**
     * Texto predefinido que interpreta el resultado segun el area
     * dominante. Tarea tecnica: "Texto que interprete el resultado
     * segun las areas dominantes".
     */
    private static final Map<String, String> INTERPRETACIONES = Map.ofEntries(
            Map.entry("ingenieria", "Te destacas por tu capacidad analítica y tu gusto por resolver problemas técnicos paso a paso. Sueles disfrutar entender cómo funcionan las cosas por dentro y construir soluciones concretas."),
            Map.entry("salud", "Tienes una fuerte orientación hacia el cuidado de las personas y el bienestar de otros. Sueles mostrar empatía, atención al detalle y disposición para ayudar en momentos difíciles."),
            Map.entry("administracion", "Muestras facilidad para organizar, liderar y tomar decisiones bajo presión. Te interesa cómo se gestionan los recursos y cómo se hacen crecer los proyectos y las organizaciones."),
            Map.entry("diseño", "Tienes una sensibilidad marcada por la estética y la creación de experiencias visuales o espaciales. Disfrutas imaginar y construir cosas que otros puedan ver, usar o habitar."),
            Map.entry("ciencias_sociales", "Te interesa profundamente entender el comportamiento humano y las dinámicas sociales. Sueles ser observador, empático y curioso sobre por qué las personas actúan como actúan."),
            Map.entry("educacion", "Disfrutas explicar, enseñar y acompañar el aprendizaje de otras personas. Tienes paciencia y facilidad para hacer que ideas complejas se entiendan de forma simple."),
            Map.entry("humanidades", "Te inclinas por la reflexión profunda, las preguntas existenciales y el pensamiento crítico. Disfrutas cuestionar, argumentar y buscarle sentido a las cosas."),
            Map.entry("derecho", "Tienes un fuerte sentido de la justicia y facilidad para argumentar y defender posiciones. Te interesa que se respeten las normas y los derechos de las personas.")
    );

    public static String nombreAmigable(String codigoArea) {
        return NOMBRES_AMIGABLES.getOrDefault(codigoArea, codigoArea);
    }

    public static String interpretacion(String codigoArea) {
        return INTERPRETACIONES.getOrDefault(codigoArea,
                "Mostraste interés en esta área a lo largo del test.");
    }
}