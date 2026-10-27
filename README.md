# Orientador Vocacional

[![CI](https://github.com/MateoChavarria/Trabajo_Ingenieria_Software/actions/workflows/ci.yml/badge.svg)](https://github.com/MateoChavarria/Trabajo_Ingenieria_Software/actions/workflows/ci.yml)

Aplicación web que ayuda a quienes están por elegir carrera. La persona se registra, responde un test de 53 preguntas, recibe su perfil por áreas, ve qué carreras encajan mejor con ella y puede explorar cómo sería cada carrera semestre por semestre.

Trabajo de Ingeniería de Software, cuarto semestre de Ingeniería de Sistemas e Informática, Universidad Pontificia Bolivariana.

**Equipo:** Miguel Ángel Higuita Hincapié, Sofia Arango Gonzalez y Mateo Chavarría Cano.
**Docente:** Yuri Marcela Escobar Cardona.

## Qué hace la aplicación

| Siempre gratis | Se desbloquea con el pago único de 20.000 COP |
|----------------|-----------------------------------------------|
| Explorar las carreras y sus planes de estudio | Test vocacional completo |
| Ver las universidades | Resumen del perfil vocacional |
| Ver el historial de tests (con sesión iniciada) | Carreras recomendadas según los resultados |
| | Simulador de carrera semestre por semestre |

- **Pagos simulados:** la pasarela es propia y solo reconoce tarjetas de prueba. No se cobra dinero real y los datos de la tarjeta nunca se guardan ni se escriben en los registros.
- **Panel de administración:** solo la cuenta administradora ve el botón "Administración" en el menú. Permite ver el resumen, los usuarios y los pagos, y gestionar carreras, universidades y el texto de las preguntas. La protección real está en el servidor (ver [SEGURIDAD.md](SEGURIDAD.md)).
- **Padres o acudientes:** no tienen cuenta ni acceso especial. Participan sentándose junto al estudiante a ver sus resultados.

## Tecnologías

Spring Boot 3.5 (Java 21), Maven, HTML y JavaScript estático servidos por el mismo backend, Supabase (Authentication y PostgreSQL), JUnit 5 y GitHub Actions.

## Estructura del repositorio

```
.
├── .github/
│   ├── workflows/ci.yml     Pipeline de integración continua: corre las pruebas en cada push y pull request
│   └── dependabot.yml       Avisos semanales de actualizaciones de dependencias (Maven y GitHub Actions)
├── .vscode/settings.json    Ajustes del editor Visual Studio Code para el soporte de Java (memoria y análisis de nulos)
├── proyecto-registro/
│   ├── pom.xml
│   ├── sql/                 Scripts de la base de datos y LEEME_orden_de_ejecucion.txt
│   └── src/
│       ├── main/java/       Código del backend (controlador, servicio, repositorio, modelo, seguridad, config)
│       ├── main/resources/  Páginas y scripts del frontend (static) y application.properties.example
│       └── test/java/       189 pruebas automáticas
├── README.md
└── SEGURIDAD.md
```

## Cómo levantar el proyecto

1. Clonar el repositorio.
2. Copiar `proyecto-registro/src/main/resources/application.properties.example` a `application.properties` en la misma carpeta. Ese archivo está en `.gitignore`: **nunca** se sube.
3. Completar en `application.properties` las credenciales de Supabase: `supabase.url`, `supabase.anonKey`, `spring.datasource.url` (con `?sslmode=require`), `spring.datasource.username` y `spring.datasource.password`. Como alternativa, se pueden dejar los marcadores `${SUPABASE_URL}`, `${SUPABASE_ANON_KEY}`, `${DB_URL}`, `${DB_USERNAME}` y `${DB_PASSWORD}` y definirlos como variables de entorno.
4. Definir en el mismo archivo el correo de la cuenta administradora: `app.admin.correo=correo@ejemplo.com`. Si se deja vacío, nadie puede entrar al panel.
5. Preparar la base de datos siguiendo el orden de `proyecto-registro/sql/LEEME_orden_de_ejecucion.txt`. Incluye `sprint11_01_trigger_usuarios.sql`, que crea automáticamente la fila en `usuarios` de cada cuenta nueva de Authentication.
6. Ejecutar desde `proyecto-registro`: `mvn spring-boot:run`
7. Abrir `http://localhost:8080`.

### Cuentas de prueba

Se crean en Supabase, en Authentication, con la opción "Auto Confirm User":

| Correo | Para qué sirve |
|--------|----------------|
| `pruebas@orientador.com` | Cuenta con acceso completo para probar todas las funciones sin pagar. Se le da acceso con `sprint10_02_utiles_demo.sql` |
| `admin@orientador.com` | Cuenta administradora, la única que ve el botón "Administración". Debe coincidir con `app.admin.correo` |

Las contraseñas no se guardan en el repositorio.

### Tarjetas de prueba de la pasarela

Sirven con cualquier nombre, un vencimiento futuro en formato MM/AA y un código de 3 o 4 dígitos.

| Número | Resultado |
|--------|-----------|
| 4242 4242 4242 4242 | Pago aprobado, desbloquea el acceso completo |
| 4000 0000 0000 0002 | Rechazado: fondos insuficientes |
| 4000 0000 0000 0069 | Rechazado: el banco emisor rechazó la tarjeta |
| Cualquier otro número válido | Rechazado porque no es una tarjeta de prueba |

## Cómo correr las pruebas

Desde `proyecto-registro`:

```
mvn clean test
```

Hay 189 pruebas: 109 unitarias y 80 de integración y de sistema. No necesitan Supabase ni base de datos, porque las pruebas de integración reemplazan esos dos extremos por versiones en memoria. Por eso también corren en GitHub, donde no existen las credenciales.

## Estado del pipeline

El pipeline de [`.github/workflows/ci.yml`](.github/workflows/ci.yml) instala Java 21 y ejecuta `mvn test` en cada push y en cada pull request. El detalle de las ejecuciones está en la pestaña [Actions](https://github.com/MateoChavarria/Trabajo_Ingenieria_Software/actions). Además, Snyk revisa las dependencias en cada pull request.

## Flujo de trabajo con ramas

Los cambios se desarrollan en una rama de trabajo (por ejemplo `feature/ci-test-vocacional`) y se integran a `devin` mediante pull request, con el pipeline en verde. No se sube directamente a `main`.

## Seguridad

Ver [SEGURIDAD.md](SEGURIDAD.md): hallazgos de la auditoría, correcciones, estado de las acciones manuales y riesgos aceptados.

## Nota de uso de IA

Durante el desarrollo de este proyecto se utilizó Claude, de Anthropic, como asistente para:

- Resolver errores de configuración de Git y GitHub (conexión del repositorio, `.gitignore`, ramas y pull requests) y configurar el pipeline de integración continua.
- Auditar la seguridad, proponer las correcciones y proteger las credenciales.
- Escribir las pruebas automáticas, el panel de administración, los ajustes de la interfaz y los scripts de base de datos.
- Redactar y revisar la documentación del repositorio, incluido este README.

La IA se usó como apoyo. Las decisiones del producto, de diseño y de alcance fueron del equipo, y todo lo que la IA propuso fue revisado, ejecutado y probado por los integrantes (189 pruebas y pipeline en verde) antes de incluirse, de acuerdo con la guía de código limpio y seguro de la asignatura.
