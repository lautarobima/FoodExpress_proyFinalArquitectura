# ADR-018: Puertos publicados al host y su justificación

- **Fecha de la decisión:** 2026-11-15
- **Estado:** Aceptado
- **Evidencia:** commit `cb08de8` (primer `docker-compose.yml` con `5432` y `5672`) y `421990c` (frontend en `3000`)
- **Registrado en el repositorio:** 2026-11-19

## Contexto
Dentro de la red de Compose los servicios se hablan por nombre y no necesitan puertos publicados. Solo se publica lo que una persona necesita alcanzar desde su máquina.

## Decisión
| Puerto | Servicio | Justificación |
|---|---|---|
| 3000 | frontend | Punto de entrada de los usuarios (navegador). |
| 8081 / 8082 / 8083 | restaurantes-menu / pedidos / notificaciones | Probar las APIs con Postman y consultar `/actuator/health` y `panel.html` sin pasar por nginx. |
| 5432 | PostgreSQL | Inspeccionar los datos con DBeaver o IntelliJ y verificar pruebas de persistencia (DB-01 a DB-06). Solo desarrollo. |
| 5672 | RabbitMQ (AMQP) | Ejecutar un servicio desde el IDE contra el broker del Compose. Solo desarrollo. |
| 15672 | Panel de RabbitMQ | Ver colas, mensajes y la DLQ durante las pruebas. |

Los puertos 5432, 5672 y 15672 se publican **solo en la interfaz local** (`127.0.0.1:...`), de modo que no quedan abiertos a otros equipos de la red.

## Opciones evaluadas
- **A. No publicar la base ni el broker:** más seguro, pero impediría inspeccionar datos y depurar un servicio desde el IDE.
- **B. Publicarlos en todas las interfaces:** cómodo, pero cualquier equipo de la red podría entrar con las credenciales por defecto.
- **C. Publicarlos solo en `127.0.0.1` (elegida).**

## Consecuencias
- (+) Facilita depuración y verificación sin exponer los puertos a la red.
- (−) Para producción, 5432, 5672, 15672 y los puertos 8081–8083 deberían dejar de publicarse y todo el tráfico entrar por el frontend o un gateway (ver también ADR-015).
