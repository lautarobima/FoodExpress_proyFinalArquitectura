# ADR-005: Mensajería asíncrona con RabbitMQ

- **Fecha de la decisión:** 2026-11-15
- **Estado:** Aceptado
- **Evidencia:** commit `cb08de8` ("Agregado RabbitMQ": `RabbitConfig`, `PedidoEventosPublisher`, listeners)
- **Registrado en el repositorio:** 2026-11-19

## Contexto
Al crear, avanzar o cancelar un pedido, otros servicios deben reaccionar: Notificaciones avisa al cliente y al restaurante, y Restaurantes y Menú ajusta el stock. Pedidos no debe esperar ni depender de que ellos estén disponibles.

## Decisión
Pedidos publica `pedido.creado`, `pedido.estado.cambiado` y `pedido.cancelado` en un exchange topic (`foodexpress.pedidos`). Cada consumidor declara sus propias colas durables. En Notificaciones, un mensaje que falla 3 veces va a una cola de mensajes fallidos (DLQ). La publicación se hace con `@TransactionalEventListener(AFTER_COMMIT)`.

## Opciones evaluadas
- **A. Llamadas REST directas a cada servicio:** simple, pero Pedidos quedaría acoplado a que todos respondan.
- **B. Apache Kafka:** muy escalable, pero excesivo para este volumen y más complejo de operar; el catálogo pide RabbitMQ.
- **C. RabbitMQ (elegida):** exchanges y routing keys encajan con eventos por tipo; fácil de levantar en Docker con panel de administración.

## Consecuencias
- (+) Notificaciones puede estar caído: los mensajes esperan en la cola (pruebas CAL-10/CAL-11).
- (+) `AFTER_COMMIT` evita anunciar pedidos que no se guardaron.
- (−) Si RabbitMQ falla justo después del commit, el pedido queda creado pero el evento se pierde (solo se registra en log). Mejora futura: patrón Outbox.
- (−) Los consumidores no son idempotentes: un mensaje duplicado podría descontar stock dos veces.
- (−) El stock queda con consistencia eventual.
