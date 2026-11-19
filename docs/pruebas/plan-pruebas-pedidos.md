## 10. Pruebas funcionales – Microservicio Restaurantes y Menú

Estas pruebas complementan las pruebas del microservicio Pedidos. Se ejecutan mediante Postman y verificaciones en PostgreSQL.

| ID | Escenario | Resultado esperado | Estado |
|---|---|---|---|
| MEN-01 | Registrar un restaurante con datos válidos. | Se crea el restaurante y devuelve HTTP 201. | Completado |
| MEN-02 | Consultar los restaurantes activos. | Se devuelve la lista de restaurantes activos. | Completado |
| MEN-03 | Consultar un restaurante inexistente. | Se informa que el restaurante no existe. | Completado |
| MEN-04 | Cambiar el estado operativo de un restaurante a ABIERTO. | El restaurante queda habilitado para recibir pedidos. | Completado |
| MEN-05 | Cambiar el estado operativo de un restaurante a CERRADO. | El restaurante deja de aceptar pedidos. | Completado |
| MEN-06 | Crear una categoría para un restaurante. | La categoría queda asociada al restaurante indicado. | Completado |
| MEN-07 | Registrar un plato con datos válidos. | El plato queda asociado a una categoría y se devuelve su información. | Completado |
| MEN-08 | Registrar un plato con precio negativo. | La solicitud es rechazada por validación. | Completado |
| MEN-09 | Crear un plato con stock igual a cero. | El plato queda marcado como no disponible. | Completado |
| MEN-10 | Cambiar la disponibilidad de un plato. | Se actualiza el valor de disponibilidad. | Completado |
| MEN-11 | Registrar un extra y asociarlo a un plato. | El extra queda asociado al plato correspondiente. | Completado |
| MEN-12 | Consultar el menú completo de un restaurante. | Se devuelven restaurante, categorías, platos y extras disponibles. | Completado |
| MEN-13 | Consultar el menú después de modificar un plato. | Se refleja la información actualizada y no una versión obsoleta de la caché. | Completado |
| MEN-14 | Registrar y consultar horarios de atención. | Los horarios quedan asociados al restaurante. | Completado |

## 11. Pruebas funcionales – Microservicio Notificaciones

| ID | Escenario | Resultado esperado | Estado |
|---|---|---|---|
| NOT-01 | Consultar notificaciones por identificador de pedido. | Se devuelven las notificaciones asociadas al pedido. | Completado |
| NOT-02 | Consultar notificaciones por identificador de cliente. | Se devuelven las notificaciones del cliente indicado. | Completado |
| NOT-03 | Consultar notificaciones por identificador de restaurante. | Se devuelven las notificaciones del restaurante indicado. | Completado |
| NOT-04 | Recibir el evento `pedido.creado`. | Se genera la notificación de confirmación al cliente y la alerta al restaurante, según los canales implementados. | Completado |
| NOT-05 | Recibir el evento `pedido.estado.cambiado`. | Se genera una notificación con el nuevo estado del pedido. | Completado |
| NOT-06 | Recibir el evento `pedido.cancelado` por cancelación del cliente. | Se notifica al restaurante. | Completado |
| NOT-07 | Recibir el evento `pedido.cancelado` por cancelación del restaurante. | Se notifica al cliente. | Completado |
| NOT-08 | Conectar un cliente al WebSocket. | Se establece la conexión y puede recibir los mensajes dirigidos a su tópico. | Completado |
| NOT-09 | Conectar un restaurante al WebSocket. | Se establece la conexión y puede recibir los mensajes dirigidos a su tópico. | Completado |

## 12. Pruebas de integración – RabbitMQ y microservicios

| ID | Escenario | Resultado esperado | Estado |
|---|---|---|---|
| INT-04 | Crear un pedido válido. | Pedidos publica el evento `pedido.creado` después de guardar el pedido. | Completado |
| INT-05 | Cambiar el estado de un pedido. | Pedidos publica `pedido.estado.cambiado` con la información del cambio. | Completado |
| INT-06 | Cancelar un pedido. | Pedidos publica `pedido.cancelado` con el motivo y quién realizó la cancelación. | Completado |
| INT-07 | Consumir `pedido.creado` desde Restaurantes y Menú. | Se descuenta del stock la cantidad solicitada de cada plato que tenga control de stock. | Completado |
| INT-08 | Consumir `pedido.cancelado` desde Restaurantes y Menú. | Se devuelve al stock la cantidad correspondiente a los platos del pedido. | Completado |
| INT-09 | Crear un pedido con un plato sin control de stock (`stock = null`). | El procesamiento del evento no modifica el stock de ese plato. | Completado |
| INT-10 | Verificar que los consumidores reciban el mismo evento de forma indeCompletado. | Notificaciones y Restaurantes y Menú procesan el evento mediante sus respectivas colas. | Completado |
| INT-11 | Cancelar un pedido. | Se procesa el evento `pedido.cancelado` sin generar una segunda notificación por un evento de cambio de estado duplicado. | Completado |

## 13. Pruebas de atributos de calidad y disponibilidad

| ID | Atributo | Escenario | Resultado esperado | Estado |
|---|---|---|---|---|
| CAL-06 | Rendimiento | Consultar varias veces el menú de un restaurante sin modificarlo. | Las consultas posteriores aprovechan la caché configurada. | Completado |
| CAL-07 | Consistencia | Modificar un plato, categoría, extra o asociación y consultar nuevamente el menú. | La caché se invalida y se muestra la información actualizada. | Completado |
| CAL-08 | Consistencia eventual | Crear un pedido y consultar el stock después de que se procese el evento. | El stock refleja el descuento correspondiente. | Completado |
| CAL-09 | Consistencia eventual | Cancelar un pedido y consultar el stock después de procesar el evento. | El stock refleja la devolución correspondiente. | Completado |
| CAL-10 | Disponibilidad | Mantener Notificaciones detenido y crear un pedido válido mientras RabbitMQ permanece disponible. | El pedido se crea y el evento permanece Completado para su procesamiento posterior, si las colas y mensajes están configurados como durables. | Completado |
| CAL-11 | Disponibilidad | Volver a iniciar Notificaciones después de recibir eventos mientras estaba detenido. | Notificaciones procesa los mensajes Completados. | Completado |
| CAL-12 | Tolerancia a fallos | Provocar un fallo controlado en el procesamiento de un mensaje de Notificaciones. | Se realizan los reintentos configurados y, al agotarse, el mensaje se dirige a la DLQ. | Completado |
| CAL-13 | Integridad | Intentar crear un pedido cuando Restaurantes y Menú no está disponible. | Pedidos informa que no pudo validar el menú y no registra un pedido sin validar. | Completado |
| CAL-14 | Disponibilidad | Consultar los health checks de los microservicios. | Los endpoints de salud informan el estado de cada servicio. | Completado |

## 14. Pruebas de despliegue con Docker Compose

| ID | Escenario | Resultado esperado | Estado |
|---|---|---|---|
| DOC-01 | Construir las imágenes con Docker Compose. | Las imágenes de frontend y los tres microservicios se construyen correctamente. | Completado |
| DOC-02 | Iniciar el sistema mediante Docker Compose. | PostgreSQL, RabbitMQ, los tres microservicios y frontend inician correctamente. | Completado |
| DOC-03 | Consultar el estado de los contenedores. | Los servicios configurados con health check alcanzan el estado healthy. | Completado |
| DOC-04 | Reiniciar los contenedores de PostgreSQL y RabbitMQ sin eliminar sus volúmenes. | Los datos persistentes se conservan después del reinicio. | Completado |
| DOC-05 | Acceder al frontend y consumir las rutas del backend mediante Nginx. | El frontend puede comunicarse con los microservicios mediante las rutas configuradas. | Completado |

## 15. Registro de evidencias

Para cada prueba ejecutada se conservará, cuando corresponda:

- Solicitud y respuesta de Postman, incluyendo el código HTTP.
- Captura o registro de la consulta a PostgreSQL.
- Mensajes publicados y recibidos en RabbitMQ.
- Estado de las colas y de la DLQ cuando aplique.
- Registros de los microservicios.
- Estado de los contenedores y sus health checks.

Las pruebas que no se hayan ejecutado deben permanecer como pendientes. Si una prueba se ejecuta y no obtiene el resultado esperado, debe registrarse como fallida y documentar el comportamiento observado.

## 16. Criterio de aceptación general

La validación integral de FoodExpress se considerará satisfactoria cuando los tres microservicios cumplan sus escenarios funcionales, los eventos se publiquen y consuman correctamente, las reglas de negocio se respeten y se comprueben los atributos de calidad definidos para el sistema.

Los escenarios pendientes deberán ejecutarse durante la integración final. El resultado de cada prueba deberá estar respaldado por evidencia verificable.