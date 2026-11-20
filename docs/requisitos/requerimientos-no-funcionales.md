# Requerimientos no funcionales (RNF)

Estado: **Implementado** / **Parcial** / **Pendiente**. La columna "Evidencia" apunta a código, ADR o prueba del plan (`docs/pruebas/plan-pruebas.md`).

| ID | Atributo | Requerimiento | Cómo se aborda en el diseño | Evidencia | Estado |
|---|---|---|---|---|---|
| RNF-01 | Disponibilidad | Los restaurantes deben poder recibir pedidos aunque Notificaciones esté caído. | Eventos por RabbitMQ con colas durables; Pedidos no espera a los consumidores. | ADR-005; CAL-10, CAL-11 | Implementado |
| RNF-02 | Tolerancia a fallos | Un mensaje que no se pueda procesar no debe bloquear la cola. | Reintentos (3) y envío a la DLQ `notificaciones.dlq` en Notificaciones. | ADR-005; CAL-12 | Implementado (solo en Notificaciones) |
| RNF-03 | Rendimiento | La consulta del menú debe responder rápido, incluso en horas pico. | Caché del menú completo con `@Cacheable` e invalidación con `@CacheEvict`. Meta propuesta: respuesta con caché por debajo de 500 ms (**por medir**). | ADR-009; CAL-06 (verifica el uso de la caché, no el tiempo) | Implementado; sin medición |
| RNF-04 | Consistencia | Los totales deben ser exactos y los platos no disponibles no pueden pedirse. | Cálculo con `BigDecimal` y redondeo `HALF_UP`; validación contra el menú en el momento de la orden; los precios se toman del servidor. | `PedidoCalculoService`; ADR-004; CAL-01 a CAL-04, PED-13 | Implementado |
| RNF-05 | Consistencia (stock) | El stock debe reflejar los pedidos y cancelaciones. | Ajuste por eventos (`pedido.creado` / `pedido.cancelado`): consistencia eventual. No se reserva de forma atómica. | ADR-005; CAL-08, CAL-09 | Parcial |
| RNF-06 | Integridad | No se debe registrar un pedido sin haber validado el menú. | Si Restaurantes y Menú no responde, Pedidos rechaza la solicitud. | ADR-004; INT-02 / CAL-13 | Implementado |
| RNF-07 | Mantenibilidad / extensibilidad | Agregar un estado de pedido o un canal de notificación sin modificar el código existente. | Patrones State y Adapter; principio abierto/cerrado. | ADR-007, ADR-008 | Implementado |
| RNF-08 | Testabilidad | La lógica de negocio debe poder probarse sin infraestructura. | Inversión de dependencias (`MenuPort`) y pruebas unitarias de estados y cálculos. | ADR-014 | Parcial (solo en Pedidos) |
| RNF-09 | Portabilidad / despliegue | Cualquier persona debe poder levantar el sistema con un solo comando. | Docker multi-stage, Compose, variables en `.env`, health checks. | ADR-010; DOC-01 a DOC-05 | Implementado |
| RNF-10 | Observabilidad | Debe poder verificarse el estado de cada componente. | `/actuator/health` en los tres servicios, health checks en Compose, panel de RabbitMQ y logs. | CAL-14, DOC-03 | Implementado (solo salud; sin métricas) |
| RNF-11 | Seguridad (secretos y despliegue) | Las credenciales no deben estar en el repositorio ni en las imágenes. | `.env` ignorado, `.env.example` versionado, variables obligatorias en Compose, contenedores sin usuario root, puertos de infraestructura solo en `127.0.0.1`. | ADR-010, ADR-018 | Implementado |
| RNF-12 | Seguridad (acceso) | Solo usuarios autorizados deben usar cada operación. | **No implementado**: los roles se eligen en la interfaz sin autenticación. | ADR-015 | Completado|
| RNF-13 | Escalabilidad | Escalar los servicios de forma independiente. | Cada servicio es un contenedor independiente. Límites: la caché local y el broker WebSocket en memoria (`enableSimpleBroker`) impiden ejecutar réplicas sin cambios. | ADR-001, ADR-009 | Parcial |
| RNF-14 | Interoperabilidad | Los servicios deben integrarse mediante contratos claros. | REST/JSON y eventos JSON; cada servicio tiene su propia copia de los records de evento. | `docs/arquitectura/api.md`, `mensajeria.md` | Implementado |
| RNF-15 | Usabilidad | El cliente debe ver el estado de su pedido sin recargar. | Notificaciones por WebSocket (STOMP) a través de nginx. | NOT-08, NOT-09, DOC-05 | Implementado |

## Atributos priorizados para FoodExpress

El catálogo prioriza **rendimiento**, **disponibilidad** y **consistencia**. Se priorizan en ese orden de importancia para el diseño: RNF-01/02 (disponibilidad), RNF-04/06 (consistencia e integridad) y RNF-03 (rendimiento).
