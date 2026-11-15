# FoodExpress – Mensajería asíncrona y patrones

## 1. Topología de RabbitMQ

```
                     foodexpress.pedidos (topic)
Pedidos ──publica──►  ├─ pedido.creado          ──► notificaciones.pedido.creado    ─┐
                      ├─ pedido.estado.cambiado ──► notificaciones.pedido.estado     ├─► Notificaciones
                      └─ pedido.cancelado       ──► notificaciones.pedido.cancelado ─┘
                                                              │ (falla 3 veces)
                                                              ▼
                                         foodexpress.dlx ──► notificaciones.dlq
```

- **Productor:** Pedidos declara solo el exchange. Cada consumidor declara sus propias colas y bindings.
- **Formato:** JSON (`JacksonJsonMessageConverter`). Cada servicio tiene su propia copia de los records del evento, así no comparten código.
- **Panel de RabbitMQ:** http://localhost:15672 (guest / guest).

## 2. Eventos

| Routing key | Cuándo | Qué hace Notificaciones |
|---|---|---|
| `pedido.creado` | Al guardar un pedido | Confirmación al cliente (email + WebSocket) y alerta al restaurante (WebSocket). |
| `pedido.estado.cambiado` | Cada `PATCH /estado` | Actualización al cliente (WebSocket). Si queda `LISTO`, también push. |
| `pedido.cancelado` | Al cancelar | Avisa a la contraparte: si canceló el cliente, al restaurante; si canceló el restaurante, al cliente. |

La cancelación publica solo `pedido.cancelado` (no también `pedido.estado.cambiado`) para no duplicar avisos.

## 3. Atributos de calidad

**Disponibilidad.** Los restaurantes siguen recibiendo pedidos aunque Notificaciones esté caído:
- Las colas son durables: si Notificaciones está apagado, los eventos esperan en RabbitMQ y se procesan cuando vuelve.
- Pedidos publica con `@TransactionalEventListener(AFTER_COMMIT)`: solo se anuncian pedidos realmente guardados, y si RabbitMQ no responde el pedido igual queda creado (el error se registra en el log).
- En Notificaciones, si un mensaje falla 3 veces va a la DLQ en lugar de bloquear la cola.

**Rendimiento.** El menú (`GET /api/restaurantes/{id}/menu`) se guarda en caché (`@Cacheable`). Cualquier cambio en restaurantes, categorías, platos, extras o sus asociaciones invalida la caché (`@CacheEvict`).

**Consistencia.** Pedidos valida contra el menú en el momento de la orden (llamada síncrona): plato/extra disponible y restaurante abierto. Los totales se calculan con `BigDecimal` y redondeo `HALF_UP`.

## 4. Patrones de diseño

| Patrón | Dónde | Problema que resuelve |
|---|---|---|
| Builder | `pedidos/patterns/builder` | Armar un pedido con varios ítems, extras y notas paso a paso. |
| State | `pedidos/patterns/state` | Cada estado define sus transiciones válidas; agregar `EnCamino` no modifica los demás estados. |
| Adapter | `notificaciones/adapter` | Email, push y WebSocket tienen APIs distintas; `CanalNotificacion` las unifica. |

## 5. SOLID

- **SRP:** el menú (Restaurantes y Menú) está separado del procesamiento de pedidos; en Pedidos, `PedidoCalculoService` solo calcula y `PedidoService` orquesta.
- **OCP:** nuevo estado = nueva clase `EstadoPedidoState`; nuevo canal = nuevo `CanalNotificacion` (Spring lo inyecta automáticamente en la lista).
- **LSP:** cualquier `EstadoPedidoState` o `CanalNotificacion` se puede usar en lugar de otro sin cambiar a quien lo usa.
- **ISP:** `MenuQueryService` expone solo la consulta del menú; las operaciones administrativas están en sus propios servicios.
- **DIP:** `MenuService` (Pedidos) depende de la interfaz `MenuPort`; `MenuClient` es solo una implementación HTTP.
