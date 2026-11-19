# ADR-005: Mensajería asíncrona con RabbitMQ

- **Fecha:** 2026-11-18
- **Estado:** Propuesto

## Contexto

Después de crear, actualizar o cancelar un pedido, otros microservicios necesitan reaccionar a estos cambios. La comunicación directa entre todos los servicios generaría mayor dependencia y dificultaría la disponibilidad.

## Decisión

Se decidió utilizar RabbitMQ para implementar comunicación asíncrona basada en eventos.

El microservicio Pedidos publica los siguientes eventos:

- `pedido.creado`
- `pedido.estado.cambiado`
- `pedido.cancelado`

Los microservicios Restaurantes y Menú y Notificaciones consumen los eventos mediante sus propias colas.

## Alternativas consideradas

- Realizar llamadas REST directas entre todos los microservicios.
- Utilizar mensajería asíncrona mediante RabbitMQ.

## Consecuencias

- Los consumidores pueden procesar los eventos de manera independiente.
- Se reduce el acoplamiento entre los servicios.
- Se permite procesar mensajes pendientes cuando un consumidor vuelve a estar disponible, si se conservan correctamente en el broker.
- Se deben gestionar errores, reintentos, mensajes duplicados y colas de mensajes fallidos.