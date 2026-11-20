# B.5 Documentación del proceso: errores, dificultades y lecciones aprendidas

## B.5.1 Errores técnicos encontrados

### Error 1: CORS al llamar a los microservicios desde el navegador
| Campo | Detalle |
|---|---|
| **Error** | El navegador bloqueaba las llamadas del frontend a las APIs publicadas en los puertos 8081, 8082 y 8083. |
| **Contexto** | Integración frontend–backend. Commit `9ffb6bc` "Agregado config para CORS" (2026-11-16). |
| **Causa raíz** | Cada puerto es un origen distinto para el navegador y los servicios no declaraban orígenes permitidos. |
| **Solución aplicada** | Primero se configuró CORS en cada servicio (`CorsPolicy`, luego unificado como `CorsConfig` en `bfe34c7`). Después, nginx en el contenedor del frontend se configuró como proxy inverso (`/api/...` y `/ws`) para que el navegador hable solo con el puerto 3000 (commit `421990c`). |
| **Lección aprendida** | Definir desde el inicio el punto de entrada del frontend (proxy o gateway) en lugar de parchear CORS servicio por servicio. |

### Error 2: Las pruebas de contexto de Spring no pueden correr durante `docker build`
| Campo | Detalle |
|---|---|
| **Error** | `mvn package` dentro del Dockerfile no puede ejecutar los tests `*ApplicationTests`. |
| **Contexto** | Etapa de build de los Dockerfiles (commit `8aafdf4`). |
| **Causa raíz** | Esos tests levantan el contexto completo de Spring y necesitan PostgreSQL y RabbitMQ, que no existen mientras se construye la imagen. |
| **Solución aplicada** | El build usa `-DskipTests` (queda comentado en el Dockerfile) y las pruebas se ejecutan por separado. |
| **Lección aprendida** | Separar las pruebas unitarias (sin infraestructura) de las de integración, y usar Testcontainers para que estas últimas puedan automatizarse. Mientras tanto, un test roto no detiene la construcción de la imagen. |

### Error 3: `curl` instalado en la imagen de ejecución solo para el health check
| Campo | Detalle |
|---|---|
| **Error** | La imagen `eclipse-temurin:21-jre` no incluye `curl`, que el health check de Compose necesita para consultar `/actuator/health`. |
| **Contexto** | Dockerfiles de los tres servicios y `docker-compose.yml`. |
| **Causa raíz** | Se eligió una imagen mínima (solo JRE) para reducir tamaño, pero el health check depende de una herramienta que esa imagen no trae. |
| **Solución aplicada** | Se instala `curl` en la etapa de ejecución (`apt-get install --no-install-recommends curl`), con un comentario que explica por qué. |
| **Lección aprendida** | Pensar el health check al diseñar la imagen. Alternativas a evaluar: un health check sin herramientas externas o la imagen base con las utilidades necesarias; el costo actual es una imagen algo mayor y más superficie de ataque. |

### Error 4: Stock con consistencia eventual
| Campo | Detalle |
|---|---|
| **Error** | El stock de un plato se descuenta después de crear el pedido, mediante un evento; dos pedidos casi simultáneos podrían superar el stock real antes de que el evento se procese. |
| **Contexto** | Flujo `pedido.creado` → Restaurantes y Menú (`docs/arquitectura/mensajeria.md`, pruebas CAL-08 y CAL-09). |
| **Causa raíz** | Para mantener la disponibilidad se eligió la mensajería asíncrona (ADR-005); la validación fuerte solo ocurre en la consulta HTTP al crear el pedido, no reserva el stock. |
| **Solución aplicada** | Se aceptó la consistencia eventual y se documentó. Los platos con stock `null` no tienen control de stock. |
| **Lección aprendida** | Cuando una regla exige exactitud (no vender más de lo que hay), la reserva debe ser atómica (por ejemplo, reservar en la misma operación de validación o usar bloqueo optimista). |

### Error 5: Notificaciones duplicadas al cancelar
| Campo | Detalle |
|---|---|
| **Error** | Cancelar un pedido podía generar dos avisos: uno por `pedido.cancelado` y otro por `pedido.estado.cambiado`, porque CANCELADO también es un estado. |
| **Contexto** | Eventos entre Pedidos y Notificaciones; prueba INT-11. |
| **Causa raíz** | Dos eventos describían el mismo hecho de negocio. |
| **Solución aplicada** | La cancelación publica solo `pedido.cancelado` (con motivo y actor). |
| **Lección aprendida** | Definir qué evento representa cada hecho de negocio antes de implementar los consumidores. |

### Error 6: Plan de pruebas con texto dañado e inconsistencias
| Campo | Detalle |
|---|---|
| **Error** | Palabras como "indeCompletado" o "permanece Completado" en el plan de pruebas; el mismo escenario figuraba como pendiente (INT-02) y como completado (CAL-13); el plan hablaba solo de Pedidos pero incluía todo el sistema. |
| **Contexto** | `docs/pruebas/plan-pruebas-pedidos.md`. |
| **Causa raíz** | Un reemplazo masivo de "pendiente" por "Completado" alteró también palabras que contenían "pendiente", y el documento creció por secciones añadidas sin una revisión global. |
| **Solución aplicada** | Se restauraron las palabras, se unificó el vocabulario de estados (Aprobado / Fallido / Pendiente) y se reorganizó el plan como `plan-pruebas.md`. |
| **Lección aprendida** | Revisar los reemplazos globales coincidencia por coincidencia y revisar los diffs de documentación como los de código. |

## B.5.2 Dificultades del proceso de equipo

- **La estrategia de Git se definió tarde.** El backend se construyó directamente sobre `main`; solo el frontend usó rama y Pull Request (PR #1). Por eso casi no hay revisión entre pares en el historial (ADR-011).
- **Trabajo concentrado en pocos commits grandes.** El commit inicial `e470f58` agregó 108 archivos y 7.233 líneas, y `421990c` ("Add files via upload") subió 3.262 líneas desde la interfaz web. Es difícil revisar y atribuir cambios así. 
- **Identidad duplicada en Git.** Un integrante aparecía como `bedoya1905` y `Bedoya1905`, lo que falsea las estadísticas por autor; se resolvió con `.mailmap`.
- **Documentación y ADRs escritos después de implementar.** Las decisiones se tomaron entre el 14 y el 18 de noviembre, pero se registraron al final. Para no inventar fechas, cada ADR indica la fecha de la decisión con el commit que la evidencia y la fecha de registro.
- **Pruebas sin dueño claro.** El plan de pruebas original asignaba escenarios a "integrante de State", "de RabbitMQ" y "de Notificaciones", y quedaron pendientes hasta integrar el trabajo de cada uno.
- **Integración de módulos.** Si bien ya conociamos el proceso de hacer un pull-request retomamos este aprendizaje

## B.5.3 Deuda técnica identificada

| Deuda | Por qué se dejó | Referencia |
|---|---|---|
| `ddl-auto=update` sin migraciones versionadas | Iterar rápido sobre un modelo cambiante | ADR-013 |
| Sin autenticación ni autorización | Fuera del alcance de tiempo; no es requisito base | ADR-015 |
| Stock con consistencia eventual | Se priorizó disponibilidad sobre exactitud inmediata | ADR-005, Error 4 |
| Si RabbitMQ falla justo después del commit, el evento se pierde (solo queda en el log) | Se priorizó que el pedido se cree igualmente; falta patrón Outbox | ADR-005 |
| Consumidores sin idempotencia (un mensaje duplicado puede descontar stock dos veces) | Pendiente de implementar | ADR-005 |
| Caché local en memoria, sin expiración | Suficiente con una sola instancia | ADR-009 |
| Credenciales de base de datos compartidas por los tres servicios | Simplicidad del entorno de curso | ADR-003 |
| Pruebas unitarias solo en Pedidos; integración manual; `-DskipTests` en el build | Plazo de entrega | ADR-014 |
| Once escenarios de prueba sin ejecutar (PED-08, PED-10, PED-11, PED-14, PED-16, CAL-05, CAL-15, DB-05, DB-06, INT-02, DOC-06) | Falta de tiempo | `plan-pruebas.md` |
| `PedidoDirector` sin uso en el flujo principal | Quedó del diseño inicial del Builder | ADR-006 |

## B.5.4 Sugerencias de mejora

**Mejoras técnicas**
1. Agregar JWT con roles detrás de un API Gateway que sea el único punto de entrada (resuelve también CORS y los puertos publicados).
2. Introducir Flyway para las migraciones y Testcontainers para automatizar las pruebas de integración, y quitar `-DskipTests` del flujo de CI.
3. Reservar el stock de forma atómica al crear el pedido, e implementar Outbox más consumidores idempotentes para no perder ni duplicar eventos.

**Mejoras de proceso**
1. Definir la estrategia de ramas, la convención de commits y la protección de `main` en la primera semana, con Pull Requests revisados por otra persona.
2. Escribir cada ADR el día que se toma la decisión.
3. Crear issues pequeñas vinculadas a los commits y repartir el trabajo en cambios de tamaño revisable.

**Trabajo futuro**
- Estado `EN_CAMINO` para pedidos a domicilio, aprovechando que el patrón State permite agregarlo sin tocar los demás estados.
- Reportes (platos más vendidos, horas pico, ingresos por restaurante) como bonificación del catálogo.
- Canales de notificación reales (correo y push) en lugar de los simulados.
