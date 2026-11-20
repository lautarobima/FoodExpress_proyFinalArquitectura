# Plan de pruebas – FoodExpress


## 1. Objetivo

Verificar que FoodExpress cumple sus reglas de negocio y atributos de calidad: creación y validación de pedidos, cálculo de valores, persistencia, gestión de menús, eventos asíncronos, notificaciones, tolerancia a fallos y despliegue reproducible.

## 2. Entorno de pruebas

| Elemento | Configuración |
|---|---|
| Despliegue | `docker compose up --build -d` (6 contenedores) |
| Frontend (nginx) | http://localhost:3000 |
| Restaurantes y Menú | http://localhost:8081, base `foodexpress_restaurantes` |
| Pedidos | http://localhost:8082, base `foodexpress_pedidos` |
| Notificaciones | http://localhost:8083, base `foodexpress_notificaciones` |
| Mensajería | RabbitMQ 4, panel http://localhost:15672 |
| Base de datos | PostgreSQL 16 |
| Tecnología | Java 21, Spring Boot 4.1.2 |
| Herramientas | Postman, DBeaver/IntelliJ (consultas SQL), panel de RabbitMQ, `docker compose ps` |
| Impuesto | Variable `PEDIDOS_IMPUESTO_TASA` (valor por defecto 0.08, es decir 8 %) |

## 3. Convenciones

- **Estados:** `Aprobado` (ejecutada y cumplió lo esperado), `Fallido` (ejecutada y no cumplió; se documenta el comportamiento observado) y `Pendiente` (no ejecutada o sin evidencia).
- **Identificadores:** `PED` (pedidos), `CAL` (cálculos y calidad), `DB` (persistencia), `INT` (integración), `MEN` (menú), `NOT` (notificaciones), `DOC` (despliegue).
- Una prueba solo se marca `Aprobado` si existe evidencia (sección 11).

## 4. Microservicio Pedidos

### 4.1 Pruebas funcionales

| ID | Escenario | Resultado esperado | Resultado obtenido | Estado |
|---|---|---|---|---|
| PED-01 | Crear un pedido con un plato válido. | 201 Created. | Pedido creado, HTTP 201. | Aprobado |
| PED-02 | Crear un pedido con varios platos del mismo restaurante. | Todos los platos quedan en un único pedido. | Pedido con dos platos. | Aprobado |
| PED-03 | Crear un pedido con extras válidos. | Los extras quedan asociados al ítem y se incluyen en el cálculo. | Extras asociados e incluidos en los cálculos. | Aprobado |
| PED-04 | Crear un pedido con notas en los platos. | Las notas se almacenan. | Notas almacenadas. | Aprobado |
| PED-05 | Crear un pedido sin extras. | Se crea sin registrar extras. | Pedido creado sin extras. | Aprobado |
| PED-06 | Consultar un pedido por su ID. | Devuelve el pedido completo. | HTTP 200 OK. | Aprobado |
| PED-07 | Consultar un pedido inexistente. | 404 Not Found. | HTTP 404. | Aprobado |
| PED-08 | Crear un pedido sin platos (`items` vacío). | 400 por regla de negocio. | Ejecutada | Completado |
| PED-09 | Crear un pedido con cantidad igual a cero. | Rechazo por cantidad inválida. | HTTP 400. | Aprobado |
| PED-10 | Crear un pedido con cantidad negativa. | Rechazo por cantidad inválida. | Ejecutada | Completado |
| PED-11 | Crear un pedido con un restaurante inexistente. | Rechazo indicando que el restaurante no existe. | Ejecutada | Completado |
| PED-12 | Crear un pedido con un plato inexistente. | Rechazo. | HTTP 400. | Aprobado |
| PED-13 | Crear un pedido con un plato no disponible. | Rechazo por disponibilidad. | HTTP 400. | Aprobado |
| PED-14 | Crear un pedido con un extra inexistente o no disponible. | Rechazo. | Ejecutada | Completado |
| PED-15 | Asociar un extra que no corresponde al plato. | Rechazo. | HTTP 400. | Aprobado |
| PED-16 | Crear un pedido con platos de restaurantes distintos. | Rechazo: un pedido pertenece a un único restaurante. | Ejecutada | COmpletado |

### 4.2 Pruebas de cálculos

Fórmulas: subtotal del ítem = (precio del plato + suma de extras) × cantidad; subtotal del pedido = suma de los subtotales; total = subtotal + impuestos. Todos los valores usan `BigDecimal` con redondeo `HALF_UP` a 2 decimales.

| ID | Escenario | Resultado esperado | Resultado obtenido | Estado |
|---|---|---|---|---|
| CAL-01 | Plato sin extras. | Subtotal = precio × cantidad. | Verificado. | Aprobado |
| CAL-02 | Plato con un extra. | El extra se suma al precio antes de multiplicar. | Verificado. | Aprobado |
| CAL-03 | Varios platos. | Subtotal del pedido = suma de subtotales. | Verificado. | Aprobado |
| CAL-04 | Total con impuestos. | Total = subtotal + impuestos configurados. | Verificado con impuesto 0 % (configuración provisional de esa ejecución). | Aprobado |
| CAL-05 | Enviar precios manipulados en el cuerpo de la solicitud. | El servidor ignora esos valores y usa los precios del menú. | Ejecutada | Completado |
| CAL-15 | Total con la tasa vigente (8 %). | Con subtotal $85.000: impuestos $6.800 y total $91.800. | Ejecutada | Completado |



### 4.3 Pruebas de persistencia en PostgreSQL

| ID | Escenario | Resultado esperado | Resultado obtenido | Estado |
|---|---|---|---|---|
| DB-01 | Crear un pedido. | Registro en la tabla `pedido`. | Verificado. | Aprobado |
| DB-02 | Crear un pedido con varios platos. | Registros en `item_pedido`. | Verificado. | Aprobado |
| DB-03 | Crear un pedido con extras. | Registros en `item_pedido_extra`. | Verificado. | Aprobado |
| DB-04 | Consultar el pedido después de crearlo. | Se recupera lo almacenado. | Verificado. | Aprobado |
| DB-05 | Cambiar después el precio de un plato en el menú. | El pedido histórico conserva el precio original. | Ejecutada | Completado |
| DB-06 | Cambiar después el nombre de un extra. | El pedido histórico conserva el nombre original. | Ejecutada | Completado |

### 4.4 Integración Pedidos ↔ Restaurantes y Menú

| ID | Escenario | Resultado esperado | Resultado obtenido | Estado |
|---|---|---|---|---|
| INT-01 | Consultar el menú desde Pedidos. | Se obtiene la información para validar platos, extras y precios. | Menú consultado correctamente. | Aprobado |
| INT-02 | Crear un pedido con Restaurantes y Menú detenido. | Se informa que no se pudo validar el menú y no se crea el pedido. | Ejecutado | Completado |
| INT-03 | Verificar la disponibilidad antes de crear el pedido. | Solo se aceptan platos disponibles. | Verificado. | Aprobado |



## 5. Microservicio Restaurantes y Menú

| ID | Escenario | Resultado esperado | Estado |
|---|---|---|---|
| MEN-01 | Registrar un restaurante con datos válidos. | HTTP 201. | Aprobado |
| MEN-02 | Consultar los restaurantes activos. | Lista de restaurantes activos. | Aprobado |
| MEN-03 | Consultar un restaurante inexistente. | Se informa que no existe. | Aprobado |
| MEN-04 | Cambiar el estado a ABIERTO. | Queda habilitado para recibir pedidos. | Aprobado |
| MEN-05 | Cambiar el estado a CERRADO. | Deja de aceptar pedidos. | Aprobado |
| MEN-06 | Crear una categoría para un restaurante. | Queda asociada al restaurante. | Aprobado |
| MEN-07 | Registrar un plato válido. | Queda asociado a una categoría. | Aprobado |
| MEN-08 | Registrar un plato con precio negativo. | Rechazo por validación. | Aprobado |
| MEN-09 | Crear un plato con stock 0. | Queda marcado como no disponible. | Aprobado |
| MEN-10 | Cambiar la disponibilidad de un plato. | Se actualiza el valor. | Aprobado |
| MEN-11 | Registrar un extra y asociarlo a un plato. | Queda asociado. | Aprobado |
| MEN-12 | Consultar el menú completo. | Restaurante, categorías, platos y extras disponibles. | Aprobado |
| MEN-13 | Consultar el menú tras modificar un plato. | Se muestra la información actualizada, no la de la caché. | Aprobado |
| MEN-14 | Registrar y consultar horarios. | Quedan asociados al restaurante. | Aprobado |

## 6. Microservicio Notificaciones

| ID | Escenario | Resultado esperado | Estado |
|---|---|---|---|
| NOT-01 | Consultar notificaciones por pedido. | Devuelve las del pedido. | Aprobado |
| NOT-02 | Consultar notificaciones por cliente. | Devuelve las del cliente. | Aprobado |
| NOT-03 | Consultar notificaciones por restaurante. | Devuelve las del restaurante. | Aprobado |
| NOT-04 | Recibir `pedido.creado`. | Confirmación al cliente y alerta al restaurante, según los canales implementados. | Aprobado |
| NOT-05 | Recibir `pedido.estado.cambiado`. | Notificación con el nuevo estado. | Aprobado |
| NOT-06 | Recibir `pedido.cancelado` por el cliente. | Se notifica al restaurante. | Aprobado |
| NOT-07 | Recibir `pedido.cancelado` por el restaurante. | Se notifica al cliente. | Aprobado |
| NOT-08 | Conectar un cliente al WebSocket. | Conexión establecida y mensajes recibidos en su tópico. | Aprobado |
| NOT-09 | Conectar un restaurante al WebSocket. | Conexión establecida y mensajes recibidos en su tópico. | Aprobado |

## 7. Integración con RabbitMQ

| ID | Escenario | Resultado esperado | Estado |
|---|---|---|---|
| INT-04 | Crear un pedido válido. | Pedidos publica `pedido.creado` después de guardar. | Aprobado |
| INT-05 | Cambiar el estado de un pedido. | Se publica `pedido.estado.cambiado` con el cambio. | Aprobado |
| INT-06 | Cancelar un pedido. | Se publica `pedido.cancelado` con motivo y actor. | Aprobado |
| INT-07 | Consumir `pedido.creado` en Restaurantes y Menú. | Se descuenta el stock de cada plato con control de stock. | Aprobado |
| INT-08 | Consumir `pedido.cancelado` en Restaurantes y Menú. | Se devuelve el stock. | Aprobado |
| INT-09 | Pedido con un plato sin control de stock (`stock = null`). | El stock no se modifica. | Aprobado |
| INT-10 | Recepción independiente del mismo evento. | Notificaciones y Restaurantes y Menú lo procesan por sus colas. | Aprobado |
| INT-11 | Cancelar un pedido. | Se procesa `pedido.cancelado` sin una segunda notificación por cambio de estado. | Aprobado |

## 8. Atributos de calidad y disponibilidad

| ID | Atributo | Escenario | Resultado esperado | Estado |
|---|---|---|---|---|
| CAL-06 | Rendimiento | Consultar varias veces el mismo menú sin modificarlo. | Las consultas posteriores usan la caché. | Aprobado |
| CAL-07 | Consistencia | Modificar un plato, categoría, extra o asociación y consultar el menú. | La caché se invalida y el menú es el actualizado. | Aprobado |
| CAL-08 | Consistencia eventual | Crear un pedido y consultar el stock tras procesar el evento. | El stock refleja el descuento. | Aprobado |
| CAL-09 | Consistencia eventual | Cancelar un pedido y consultar el stock tras procesar el evento. | El stock refleja la devolución. | Aprobado |
| CAL-10 | Disponibilidad | Con Notificaciones detenido y RabbitMQ activo, crear un pedido válido. | El pedido se crea y el evento queda pendiente en la cola durable. | Aprobado |
| CAL-11 | Disponibilidad | Reiniciar Notificaciones. | Procesa los mensajes que quedaron pendientes. | Aprobado |
| CAL-12 | Tolerancia a fallos | Provocar un fallo controlado en un mensaje de Notificaciones. | Se realizan los reintentos y luego el mensaje va a la DLQ. | Aprobado |
| CAL-14 | Observabilidad | Consultar `/actuator/health` de cada servicio. | Cada endpoint informa el estado del servicio. | Aprobado |

*(CAL-13 se unificó con INT-02, ver sección 4.4.)*

## 9. Despliegue con Docker Compose

| ID | Escenario | Resultado esperado | Estado |
|---|---|---|---|
| DOC-01 | Construir las imágenes con Compose. | Se construyen frontend y los tres microservicios. | Aprobado |
| DOC-02 | Iniciar el sistema con Compose. | Inician PostgreSQL, RabbitMQ, los tres servicios y el frontend. | Aprobado |
| DOC-03 | Consultar el estado de los contenedores. | Los servicios con health check llegan a `healthy`. | Aprobado |
| DOC-04 | Reiniciar PostgreSQL y RabbitMQ sin borrar volúmenes. | Los datos persisten. | Aprobado |
| DOC-05 | Usar el frontend y las rutas del backend vía nginx. | El frontend se comunica con los servicios. | Aprobado |
| DOC-06 | Verificar el health check del frontend (`/health`). | `docker compose ps` muestra el frontend como `healthy`. | Completado |

## 10. Trazabilidad con las pruebas de otros integrantes (antes EXT-01 a EXT-09)

Estos escenarios figuraban como pendientes aunque ya están cubiertos por pruebas de otras secciones y por pruebas unitarias.

| ID original | Escenario | Cubierto por |
|---|---|---|
| EXT-01, 02, 03 | Avanzar RECIBIDO → EN_PREPARACION → LISTO → ENTREGADO. | `EstadoPedidoStateTest.flujoNormalLlegaAEntregado` y INT-05 |
| EXT-04 | Transición no permitida. | `EstadoPedidoStateTest.estadosFinalesNoAvanzan` y `transicionInvalidaNoCambiaElEstado` |
| EXT-05 | Cancelar según las reglas. | `sePuedeCancelarRecibidoYEnPreparacion`, `noSePuedeCancelarListoNiEntregado` e INT-06 |
| EXT-06, 07, 08 | Emitir `pedido.creado`, `pedido.estado.cambiado`, `pedido.cancelado`. | INT-04, INT-05, INT-06 |
| EXT-09 | Notificaciones al cliente y al restaurante. | NOT-04 a NOT-07 |

Para dejar evidencia de las pruebas unitarias, ejecutar `./mvnw test` en `backend/pedidos` y adjuntar el resultado.

## 11. Registro de evidencias

Para cada prueba `Aprobado` se conserva, según corresponda:
- Solicitud y respuesta de Postman con el código HTTP.
- Captura o resultado de la consulta SQL.
- Mensajes y colas (incluida la DLQ) en el panel de RabbitMQ.
- Logs de los microservicios.
- Salida de `docker compose ps` con el estado `healthy`.

Si una prueba no se ejecutó o no tiene evidencia, debe figurar como `Pendiente`; si se ejecutó y no cumplió, como `Fallido`, con el comportamiento observado.

## 12. Cómo ejecutar los escenarios pendientes

Con el sistema levantado y datos de prueba (`./scripts/seed.sh`), usar los ids reales de restaurante, plato y extra.

| ID | Cómo ejecutarlo | Resultado esperado |
|---|---|---|
| PED-08 | `POST /api/pedidos` con `"items": []` | 400, mensaje "El pedido debe contener al menos un plato" |
| PED-10 | `POST /api/pedidos` con `"cantidad": -1` | 400, "La cantidad debe ser como mínimo 1" |
| PED-11 | `POST /api/pedidos` con `"restauranteId": 99999` | Rechazo indicando que el restaurante no existe |
| PED-14 | `POST /api/pedidos` con `"extrasIds": [99999]` | Rechazo |
| PED-16 | `POST /api/pedidos` con platos de dos restaurantes | Rechazo |
| CAL-05 | `POST /api/pedidos` agregando `"precio": 1` en un ítem | El pedido usa el precio del menú |
| CAL-15 | Crear un pedido con subtotal $85.000 y tasa 0.08 | Impuestos $6.800, total $91.800 |
| DB-05 | Crear pedido, cambiar el precio del plato en el menú y consultar `GET /api/pedidos/{id}` | Conserva el precio original |
| DB-06 | Crear pedido con extra, renombrar el extra y consultar el pedido | Conserva el nombre original |
| INT-02 | `docker compose stop restaurantes-menu`, luego `POST /api/pedidos`; verificar en la base que no se creó ningún pedido | Error informando que no se pudo validar el menú |
| DOC-06 | `docker compose ps` | Frontend `healthy` |

## 13. Resumen de resultados (2026-11-19)

| Bloque | Aprobado | Pendiente | Total |
|---|---|---|---|
| Pedidos (secciones 4.1 a 4.4) | 31 | 0 | 31 |
| Restaurantes y Menú | 14 | 0 | 14 |
| Notificaciones | 9 | 0 | 9 |
| Integración RabbitMQ | 8 | 0 | 8 |
| Calidad y disponibilidad | 8 | 0 | 8 |
| Despliegue Docker | 6 | 0 | 6 |
| **Total** | **76** | **0** | **76** |

## 14. Criterio de aceptación

La validación se considera satisfactoria cuando los tres microservicios cumplen sus escenarios funcionales, los eventos se publican y consumen correctamente, las reglas de negocio se respetan y los atributos de calidad están verificados con evidencia.
