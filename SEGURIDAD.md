# Auditoría de seguridad: Orientador Vocacional

Revisión del backend Spring Boot, el frontend HTML y JavaScript y Supabase. La auditoría inicial se hizo sobre la rama `main`. Cada hallazgo indica severidad, ubicación, riesgo y la corrección aplicada en las ramas `devin` y `feature/ci-test-vocacional`. Este documento se actualizó el 26 de octubre de 2026.

Severidades: **Crítica**, **Alta**, **Media**, **Baja**.

## Estado de las acciones manuales

Son acciones que no se pueden resolver con código y que el equipo debía hacer en Supabase o en su computador.

| Acción | Estado |
|--------|--------|
| Cambiar la contraseña de la base de datos de Supabase | Hecho |
| Rotar la clave pública de Supabase | Hecho |
| Ejecutar `sql/seguridad_rls.sql` (RLS en las 10 tablas) | Hecho. Se verificó que las 10 tablas muestran `rowsecurity` verdadero |
| Ejecutar `sql/sprint11_01_trigger_usuarios.sql` | Pendiente de ejecutar en cada base nueva (ver H-20) |
| Exigir confirmación de correo en Authentication | Hecho |
| Longitud mínima de 8 y requisitos de contraseña en Authentication | Hecho: minúsculas, mayúsculas y dígitos, igual que la validación del servidor |
| Protección contra contraseñas filtradas | No disponible: Supabase la ofrece solo en el plan Pro. Riesgo aceptado |
| Captcha en login y registro | No activado a propósito. Ya existe un límite de 10 intentos por minuto por IP (H-08) |
| Credenciales fuera del repositorio | Se usa un `application.properties` local que no se sube (está en `.gitignore`). Las credenciales anteriores ya fueron cambiadas |
| Limpiar el historial de git con `git filter-repo` o BFG | No se hace. Como las credenciales antiguas ya fueron rotadas, aunque sigan en el historial ya no sirven |
| Servir la aplicación por HTTPS | No aplica mientras se ejecute en un computador local. Será obligatorio si se despliega |

## Hallazgos

| ID | Severidad | Hallazgo | Ubicación | Corrección |
|----|-----------|----------|-----------|------------|
| H-01 | Crítica | **Credenciales expuestas en el repositorio.** `application.properties` se subió con la URL y la clave pública de Supabase (commits `d820a34`, `cd4d181`, `8562a6c`) y, en el commit `8562a6c`, también con el usuario y la **contraseña de PostgreSQL**. Siguen en el historial aunque luego se agregó al `.gitignore`. | Historial de git | Plantilla `application.properties.example`; `.gitignore` ampliado (`.env`, `*.key`, `application-*.properties`). Las credenciales se rotaron, por lo que las del historial ya no sirven. |
| H-02 | Crítica | **Sin autenticación en el backend / IDOR.** El servidor nunca verificaba el token de sesión. `POST /api/test/resultado` recibía el `usuarioId` en el cuerpo y `GET /api/perfil/historial?usuarioId=` lo recibía por URL: cualquiera podía leer el historial de otra persona o guardar resultados a nombre de otro. | `TestController`, `PerfilController`, `js/test.js`, `js/perfil.js` | `InterceptorAutenticacion`: exige `Authorization: Bearer <token>` en las rutas privadas y valida el token contra Supabase (`/auth/v1/user`, con caché de 60 s que guarda solo el hash SHA-256 del token). Los controladores toman el id **del token validado**. Lo cubren las pruebas PI 07. |
| H-03 | Alta | **Tablas expuestas por la API REST de Supabase.** La clave pública permite consultar las tablas por PostgREST si no tienen RLS. | Supabase / `sql/` | `sql/seguridad_rls.sql`: activa RLS en las 10 tablas, revoca permisos a `anon` y `authenticated` (el backend usa JDBC con el rol dueño, no se afecta) y agrega un índice único por `lower(correo)`. |
| H-04 | Alta | **Cross-Site Scripting (XSS).** Se insertaban datos con `innerHTML`. | `static/js/*.js` | `js/utilidadesDom.js` (`crearElemento`, `mostrarAlerta`, `crearBarraAfinidad`) usa `textContent`. Se eliminó `innerHTML` con datos variables. Las pantallas nuevas, como el panel de administración, siguen la misma regla. |
| H-05 | Alta | **Open redirect / XSS por `?volver=`.** | `js/login.js` | `destinoSeguroDespuesDeLogin()` solo acepta páginas de una lista blanca. |
| H-06 | Media | **CORS abierto a cualquier origen.** | `ConfiguracionCors` | Por defecto no se habilita ningún origen externo. Orígenes configurables en `app.cors.allowed-origins`; `*` se ignora. Solo GET y POST. |
| H-07 | Media | **Sin cabeceras de seguridad HTTP.** | Todas las respuestas | `FiltroCabecerasSeguridad`: `Content-Security-Policy` (`script-src 'self'`), `X-Frame-Options: DENY`, `X-Content-Type-Options`, `Referrer-Policy`, `Permissions-Policy`, HSTS bajo HTTPS. Ver también H-21 para la política de caché. |
| H-08 | Media | **Sin límite de intentos en login y registro.** | `/api/auth/**` | `FiltroLimiteIntentos`: máximo 10 POST por IP por minuto (configurable), responde `429`. |
| H-09 | Media | **Sesión sin expiración real en el cliente.** | `SesionUsuario`, `js/sesion.js` | `SesionUsuario` usa `Instant` (UTC) y el `expires_in` real de Supabase. `obtenerSesion()` descarta sesiones expiradas; un `401` cierra la sesión. |
| H-10 | Media | **Conexiones sin cifrado garantizado.** | `application.properties`, `ConfiguracionSupabase` | `?sslmode=require` y arranque bloqueado si `supabase.url` no es HTTPS (salvo localhost). Las contraseñas **no** se guardan en la tabla `usuarios`: Supabase Auth las almacena con bcrypt. |
| H-11 | Media | **Dependencias desactualizadas** (Spring Boot 3.3.4 con CVE publicados). | `pom.xml` | Actualizado a Spring Boot **3.5.16**. `.github/dependabot.yml` avisa cada semana y Snyk revisa cada pull request. |
| H-12 | Baja | **Validación de entradas insuficiente.** | Controladores, `Usuario` | `ValidadorAfinidad`, límite de 500 respuestas, ids positivos, correo de máximo 254 caracteres y contraseña de máximo 72, regex de correo corregido. El servidor además exige el cuestionario **completo** y que cada opción pertenezca a su pregunta (prueba PU 35). |
| H-13 | Baja | **Fuga de información en errores.** | `SupabaseServicioAutenticacion`, controladores | `ManejadorGlobalErrores` devuelve mensajes genéricos y registra el detalle en el servidor. `server.error.include-*=never`. |
| H-14 | Baja | **Peticiones a Supabase sin timeout.** | `SupabaseServicioAutenticacion` | Timeout de 15 s por petición. |
| H-15 | Baja | **SQL visible en logs** y `open-in-view` activo. | Configuración | `show-sql=false` y `open-in-view=false` en la plantilla. |
| H-16 | Baja | **Pipeline CI con permisos por defecto.** | `.github/workflows/ci.yml` | `permissions: contents: read`, `persist-credentials: false` y caché de Maven. |
| H-17 | Info | El README pedía copiar `application.properties.example`, pero el archivo no estaba. | README | Plantilla restaurada y README actualizado. |
| H-18 | Alta | **Funciones de pago sin control en el servidor.** Una función de pago se podía usar llamando directamente a la API, aunque el botón estuviera bloqueado en la interfaz. | `InterceptorAccesoCompleto`, `ConfiguracionSeguridad` | El servidor responde 403 en las cinco rutas de pago a quien no pagó, 401 sin sesión y 503 si no puede verificar el acceso. El acceso es por usuario. La pasarela es simulada, `DatosTarjeta` oculta el número y el código en su representación en texto y nunca se guardan ni se registran. Lo cubren las pruebas de seguridad e integración de pagos. |
| H-19 | Alta | **Panel de administración.** Ocultar el botón en la interfaz no protege nada, porque cualquiera puede escribir la dirección o llamar a `/api/admin/**`. | `InterceptorAdmin`, `ConfiguracionAdmin`, `ServicioAdmin`, `JdbcRepositorioAdmin` | El servidor comprueba en **cada** petición que la sesión sea válida y que el correo sea exactamente el de `app.admin.correo`. Responde 403 a cualquier otro usuario, y si el correo no está configurado nadie entra. La comparación es exacta (no sirven alias como `admin+1@...`). Los datos se validan en el servidor y las consultas SQL usan parámetros. El correo vive en el `application.properties` local, no en el repositorio. Cubierto por `AdminIntegracionTest` y `ConfiguracionAdminTest`. |
| H-20 | Media | **Usuarios sin fila en `usuarios`.** Una cuenta creada desde Authentication de Supabase no pasa por el registro de la aplicación, no tiene fila en `usuarios` y el pago falla por la llave foránea de `pagos`. | `sql/sprint11_01_trigger_usuarios.sql` | Un trigger crea la fila automáticamente cuando nace un usuario en `auth.users` (función `SECURITY DEFINER` con `search_path` fijo) y el script rellena las filas faltantes. |
| H-21 | Baja | **Política de caché.** Para mejorar la velocidad se agregó memoria temporal y caché del navegador. | `CacheTemporal`, `FiltroCabecerasSeguridad` | Solo el catálogo público, igual para todos (carreras, plan de estudios, universidades), puede guardarse cinco minutos. Todo dato privado o de pago mantiene `no-store`, y hay pruebas ruta por ruta. En el servidor solo se recuerda el "sí tiene acceso" de un usuario (el "no" siempre se consulta) y las memorias se vacían cuando el administrador modifica datos. |

## Guía de código limpio y seguro

Se revisó el código con la guía de referencia de la asignatura. Ya se cumplía: sin bloques `catch` vacíos ni código comentado, logs sin contraseñas, tokens ni datos de tarjeta, mensajes de login genéricos, validación en el servidor, secretos fuera del repositorio y análisis de dependencias en el pipeline. Se corrigieron los números mágicos (códigos HTTP y otros valores pasaron a constantes con nombre), la duplicación de manejo de errores del panel y el nombre engañoso `CasoUsoCalcularYGuardarResultadoTest`, que ahora es `CasoUsoCalcularYGuardarResultadoVocacional`.

## Riesgos aceptados y pendientes

- **Enumeración de correos en el registro:** responder "correo ya registrado" lo exige el criterio CA-02 de la Historia 1. El límite de intentos (H-08) reduce el abuso.
- **Token en `localStorage`:** es accesible para JavaScript si hubiera un XSS. Se mitiga con H-04 y la política de contenido de H-07. La alternativa (cookie `HttpOnly` con protección CSRF) implica rediseñar la sesión.
- **Cuenta administradora:** su seguridad depende de que use una contraseña fuerte. Se recomienda no compartirla.
- **`/api/perfil/resumen` y `/api/carreras/recomendadas`** calculan a partir de la afinidad que envía el cliente. Un usuario podría manipular *su propia* vista, pero no la de otros ni lo guardado en la base de datos, que calcula el servidor en `/api/test/resultado`.
- **Cerrar sesión** borra la sesión local. El token de Supabase sigue siendo válido hasta que expira (máximo 1 hora por defecto).
- El limitador de intentos es en memoria (una sola instancia del servidor).
- **Ley 1581 de 2012:** falta agregar en el registro la autorización explícita para el tratamiento de datos personales y su finalidad. La aplicación ya recolecta el mínimo (correo y resultados del test).
- **HTTPS** y cifrado en tránsito si la aplicación se despliega, y bloqueo de contraseñas filtradas si se pasa a un plan de pago de Supabase.

## Pruebas

`mvn clean test` ejecuta 189 pruebas: 109 unitarias y 80 de integración y de sistema. Cubren el interceptor de autenticación, el control de acceso por pago, el panel de administración, el limitador de intentos, las cabeceras de seguridad y la política de caché, la validación de afinidad y del cuestionario, las reglas de correo y contraseña, la pasarela y el flujo completo del sistema. El pipeline de GitHub las ejecuta en cada push y pull request.
