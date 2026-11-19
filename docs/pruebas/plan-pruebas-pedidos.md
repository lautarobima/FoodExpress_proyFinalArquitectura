# Plan de pruebas – Microservicio Pedidos

## 1. Objetivo

Definir los escenarios de prueba necesarios para verificar el funcionamiento del microservicio Pedidos de FoodExpress, comprobando la creación de pedidos, validación de platos y extras, cálculo de valores, almacenamiento en PostgreSQL y consulta de pedidos.

Las pruebas se realizarán principalmente mediante Postman y consultas a PostgreSQL.

## 2. Entorno de pruebas

| Elemento | Configuración |
|---|---|
| Microservicio | Pedidos |
| Puerto | 8082 |
| Base de datos | foodexpress_pedidos |
| Tecnología | Spring Boot |
| Base de datos | PostgreSQL |
| Herramienta para API | Postman |
| Microservicio externo | Restaurantes y Menú (puerto 8081) |

## 3. Pruebas funcionales

| ID | Escenario | Resultado esperado |
|---|---|---|
| PED-01 | Crear un pedido con un plato válido. | El pedido se crea correctamente y devuelve 201 Created. |
| PED-02 | Crear un pedido con varios platos del mismo restaurante. | Se registran todos los platos dentro de un único pedido. |
| PED-03 | Crear un pedido incluyendo extras válidos. | Los extras quedan asociados al plato correspondiente y sus precios se incluyen en el cálculo. |
| PED-04 | Crear un pedido incluyendo notas para los platos. | Las notas se almacenan correctamente. |
| PED-05 | Crear un pedido sin extras. | El pedido se crea correctamente sin registrar extras. |
| PED-06 | Consultar un pedido existente por su ID. | Se devuelve la información completa del pedido. |
| PED-07 | Consultar un pedido con un ID inexistente. | Se devuelve 404 Not Found. |
| PED-08 | Crear un pedido sin platos. | Se rechaza la solicitud por incumplir la regla de negocio. |
| PED-09 | Crear un pedido con cantidad igual a cero. | Se rechaza la solicitud por cantidad inválida. |
| PED-10 | Crear un pedido con cantidad negativa. | Se rechaza la solicitud por cantidad inválida. |
| PED-11 | Crear un pedido con un restaurante inexistente. | Se rechaza la solicitud y se informa que el restaurante no existe. |
| PED-12 | Crear un pedido con un plato inexistente. | Se rechaza la solicitud. |
| PED-13 | Crear un pedido con un plato no disponible. | Se rechaza la solicitud por incumplir la disponibilidad del menú. |
| PED-14 | Crear un pedido con un extra inexistente o no disponible. | Se rechaza la solicitud. |
| PED-15 | Asociar un extra que no corresponde al plato seleccionado. | Se rechaza la solicitud. |
| PED-16 | Intentar crear un pedido con platos de restaurantes diferentes. | Se rechaza la solicitud, porque un pedido pertenece a un único restaurante. |

## 4. Pruebas de cálculos

| ID | Escenario | Resultado esperado |
|---|---|---|
| CAL-01 | Crear un pedido con un plato sin extras. | El subtotal corresponde al precio del plato multiplicado por su cantidad. |
| CAL-02 | Crear un pedido con un plato y un extra. | El precio del extra se suma al precio del plato antes de multiplicar por la cantidad. |
| CAL-03 | Crear un pedido con varios platos. | El subtotal del pedido corresponde a la suma de los subtotales de sus elementos. |
| CAL-04 | Verificar el total del pedido. | El total corresponde al subtotal más los impuestos configurados. |
| CAL-05 | Enviar precios manipulados desde el cliente. | El servidor ignora esos valores y utiliza los precios consultados al microservicio de menú. |

**Fórmulas de referencia:**

- Subtotal del elemento = (precio del plato + suma de extras) × cantidad.
- Subtotal del pedido = suma de los subtotales de sus elementos.
- Total = subtotal del pedido + impuestos.

La tarifa de impuestos queda pendiente de definición por el equipo. No se establecerá un porcentaje definitivo hasta acordarlo.

## 5. Pruebas de persistencia en PostgreSQL

| ID | Escenario | Resultado esperado |
|---|---|---|
| DB-01 | Crear un pedido. | Se registra correctamente en la tabla `pedido`. |
| DB-02 | Crear un pedido con varios platos. | Todos los elementos quedan registrados en `item_pedido`. |
| DB-03 | Crear un pedido con extras. | Los extras quedan registrados en `item_pedido_extra`. |
| DB-04 | Consultar el pedido después de crearlo. | Se recupera la información almacenada en la base de datos. |
| DB-05 | Modificar posteriormente el precio de un plato en el menú. | El pedido histórico conserva el precio registrado al momento de su creación. |
| DB-06 | Modificar posteriormente el nombre de un extra en el menú. | El pedido histórico conserva el nombre registrado al momento de su creación. |

## 6. Pruebas de integración

| ID | Escenario | Resultado esperado |
|---|---|---|
| INT-01 | Consultar el menú desde Pedidos. | Se obtiene la información necesaria para validar platos, extras y precios. |
| INT-02 | Intentar crear un pedido cuando Restaurantes y Menú no responde. | Se informa que no fue posible validar el menú y no se crea un pedido con información sin verificar. |
| INT-03 | Verificar la disponibilidad de un plato antes de crear el pedido. | Solo se permite crear pedidos con platos disponibles. |

## 7. Pruebas correspondientes a otros integrantes

Las siguientes pruebas se registran como parte de la integración general, pero su implementación corresponde a los responsables de State y Notificaciones.

| ID | Escenario | Responsable |
|---|---|---|
| EXT-01 | Cambiar un pedido de RECIBIDO a EN_PREPARACION. | Integrante de State |
| EXT-02 | Cambiar un pedido de EN_PREPARACION a LISTO. | Integrante de State |
| EXT-03 | Cambiar un pedido de LISTO a ENTREGADO. | Integrante de State |
| EXT-04 | Intentar una transición de estado no permitida. | Integrante de State |
| EXT-05 | Cancelar un pedido según las reglas del negocio. | Integrante de State |
| EXT-06 | Emitir el evento pedido.creado. | Integrante de RabbitMQ |
| EXT-07 | Emitir el evento pedido.estado.cambiado. | Integrante de RabbitMQ |
| EXT-08 | Emitir el evento pedido.cancelado. | Integrante de RabbitMQ |
| EXT-09 | Verificar el envío de notificaciones al cliente y restaurante. | Integrante de Notificaciones |

## 8. Registro de resultados

Las pruebas se ejecutaron mediante Postman y consultas a PostgreSQL. Los resultados se clasifican según la evidencia obtenida durante la validación del microservicio.

### 8.1. Pruebas funcionales

| ID | Resultado obtenido | Estado |
|---|---|---|
| PED-01 | Pedido creado correctamente. HTTP 201 Created. | Aprobado |
| PED-02 | Se creó un pedido con dos platos del mismo restaurante. | Aprobado |
| PED-03 | Los extras válidos quedaron asociados a sus respectivos ítems y se incluyeron en los cálculos. | Aprobado |
| PED-04 | Las notas de los platos se almacenaron correctamente. | Aprobado |
| PED-05 | Se creó un pedido con platos sin extras seleccionados. | Aprobado |
| PED-06 | La consulta de un pedido existente devolvió HTTP 200 OK. | Aprobado |
| PED-07 | La consulta de un pedido inexistente devolvió HTTP 404 Not Found. | Aprobado |
| PED-08 | No ejecutada. | Pendiente |
| PED-09 | Una cantidad igual a cero fue rechazada con HTTP 400 Bad Request. | Aprobado |
| PED-10 | No ejecutada. | Pendiente |
| PED-11 | No ejecutada. | Pendiente |
| PED-12 | Un identificador de plato inexistente fue rechazado con HTTP 400 Bad Request. | Aprobado |
| PED-13 | Un plato no disponible fue rechazado con HTTP 400 Bad Request. | Aprobado |
| PED-14 | No ejecutada con un identificador de extra inexistente o un extra no disponible. | Pendiente |
| PED-15 | Un extra asociado a otro plato fue rechazado con HTTP 400 Bad Request. | Aprobado |
| PED-16 | No ejecutada. | Pendiente |

### 8.2. Pruebas de cálculos

| ID | Resultado obtenido | Estado |
|---|---|---|
| CAL-01 | Se verificó el subtotal de platos sin extras. | Aprobado |
| CAL-02 | Se verificó que el precio de los extras se incluye antes de multiplicar por la cantidad. | Aprobado |
| CAL-03 | Se verificó la suma de los subtotales de varios platos. | Aprobado |
| CAL-04 | Se verificó que el total corresponde al subtotal más los impuestos. | Aprobado |
| CAL-05 | No ejecutada. | Pendiente |

Los pedidos de prueba registraron subtotales y totales de $85.000 y $91.000, respectivamente. En ambos casos los impuestos fueron $0.00, de acuerdo con la configuración provisional.

### 8.3. Pruebas de persistencia

| ID | Resultado obtenido | Estado |
|---|---|---|
| DB-01 | Se verificó el registro de pedidos en la tabla `pedido`. | Aprobado |
| DB-02 | Se verificó el registro de los platos en `item_pedido`. | Aprobado |
| DB-03 | Se verificó el registro de extras en `item_pedido_extra`. | Aprobado |
| DB-04 | Se consultaron pedidos almacenados previamente en PostgreSQL. | Aprobado |
| DB-05 | No ejecutada. | Pendiente |
| DB-06 | No ejecutada. | Pendiente |

### 8.4. Pruebas de integración

| ID | Resultado obtenido | Estado |
|---|---|---|
| INT-01 | Se consultó correctamente el menú del restaurante desde el microservicio Pedidos. | Aprobado |
| INT-02 | No ejecutada. | Pendiente |
| INT-03 | Se verificó que un plato no disponible no puede incluirse en un pedido. | Aprobado |

### 8.5. Pruebas correspondientes a otros integrantes

Las pruebas de transiciones de estado, cancelaciones, eventos RabbitMQ y notificaciones quedan pendientes de implementación y validación por los integrantes responsables de dichas funcionalidades.

| ID | Responsable | Estado |
|---|---|---|
| EXT-01 a EXT-05 | Integrante de State | Pendiente |
| EXT-06 a EXT-08 | Integrante de RabbitMQ | Pendiente |
| EXT-09 | Integrante de Notificaciones | Pendiente |

## 9. Criterios de aceptación

El microservicio Pedidos cumple con los escenarios de su alcance que fueron implementados y probados: creación de pedidos con uno o varios platos, validación de disponibilidad, registro de extras y notas, cálculo de valores, persistencia en PostgreSQL y consulta de pedidos.

Los escenarios registrados como pendientes deberán ejecutarse durante las siguientes etapas de validación e integración del proyecto.
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
| INT-10 | Verificar que los consumidores reciban el mismo evento de forma independiente. | Notificaciones y Restaurantes y Menú procesan el evento mediante sus respectivas colas. | Completado |
| INT-11 | Cancelar un pedido. | Se procesa el evento `pedido.cancelado` sin generar una segunda notificación por un evento de cambio de estado duplicado. | Completado |

## 13. Pruebas de atributos de calidad y disponibilidad

| ID | Atributo | Escenario | Resultado esperado | Estado |
|---|---|---|---|---|
| CAL-06 | Rendimiento | Consultar varias veces el menú de un restaurante sin modificarlo. | Las consultas posteriores aprovechan la caché configurada. | Completado |
| CAL-07 | Consistencia | Modificar un plato, categoría, extra o asociación y consultar nuevamente el menú. | La caché se invalida y se muestra la información actualizada. | Completado |
| CAL-08 | Consistencia eventual | Crear un pedido y consultar el stock después de que se procese el evento. | El stock refleja el descuento correspondiente. | Completado |
| CAL-09 | Consistencia eventual | Cancelar un pedido y consultar el stock después de procesar el evento. | El stock refleja la devolución correspondiente. | Completado |
| CAL-10 | Disponibilidad | Mantener Notificaciones detenido y crear un pedido válido mientras RabbitMQ permanece disponible. | El pedido se crea y el evento permanece pendiente para su procesamiento posterior, si las colas y mensajes están configurados como durables. | Completado |
| CAL-11 | Disponibilidad | Volver a iniciar Notificaciones después de recibir eventos mientras estaba detenido. | Notificaciones procesa los mensajes pendientes. | Completado |
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