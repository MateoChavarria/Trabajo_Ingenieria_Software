package com.orientadorvocacional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion.
 *
 * Al ejecutar este main(), Spring Boot:
 *   1) levanta un servidor web embebido (Tomcat) en el puerto configurado
 *      en application.properties,
 *   2) escanea el paquete "com.orientadorvocacional" buscando clases
 *      anotadas con @Service, @Repository, @RestController, etc. y las
 *      registra automaticamente,
 *   3) sirve los archivos estaticos (HTML/JS/CSS) que esten en
 *      src/main/resources/static/.
 *
 * Reemplaza a la antigua clase Main.java que abria una ventana Swing:
 * ahora la interfaz vive en el navegador, no en este proceso Java.
 */
   @SpringBootApplication
   public class OrientadorVocacionalApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrientadorVocacionalApplication.class, args);
    }
}
