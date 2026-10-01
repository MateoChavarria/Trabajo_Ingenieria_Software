# Trabajo_Ingenieria_Software
Este es un espacio destinado al desarrollo del trabajo de Ingeniería de software parara el cuarto semestre.
 
## Cómo levantar el proyecto
1. Clonar el repositorio
2. Copiar `src/main/resources/application.properties.example` a `application.properties`
3. Llenar las credenciales de Supabase (URL, Publishable key) y de la base de datos (host, usuario, contraseña del pooler)
4. Ejecutar: `mvn spring-boot:run`
5. Abrir `http://localhost:8080/registro.html`

## Cómo correr las pruebas
mvn test

## Estado del pipeline
[Badge o enlace a la pestaña Actions]

## Nota de uso de IA
[Durante el desarrollo de este proyecto se utilizaron herramientas de inteligencia artificial (Claude, de Anthropic) como apoyo para:

- Resolver errores de configuración de Git y GitHub (conexión del repositorio, `.gitignore`, ramas y Pull Requests).
- Orientar la configuración del pipeline de integración continua y la protección de credenciales (`application.properties.example`).
- Redactar y revisar la documentación del repositorio (este README).

La IA se usó únicamente como asistente. El diseño, el código del proyecto, las decisiones técnicas y la revisión final fueron realizados por el equipo, y todo lo sugerido por la IA fue verificado y probado por los integrantes antes de incluirse.]