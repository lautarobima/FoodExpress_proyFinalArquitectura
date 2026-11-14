# FoodExpress – Documentación de API

## 1. Microservicio Restaurantes y Menú

**Puerto local:** `8081`  
**URL base:** `http://localhost:8081`

Este microservicio administra la información de los restaurantes, sus horarios de atención, categorías, platos y extras disponibles.

### 1.1. Restaurantes

**Controlador:** `RestauranteController`  
**Ruta base:** `/api/restaurantes`

| Método HTTP | Endpoint | Descripción | Respuesta esperada |
|---|---|---|---|
| POST | `/api/restaurantes` | Registrar un restaurante. | 201 Created |
| GET | `/api/restaurantes` | Listar restaurantes activos. | 200 OK |
| GET | `/api/restaurantes/{id}` | Consultar un restaurante por su ID. | 200 OK |
| PUT | `/api/restaurantes/{id}` | Actualizar la información de un restaurante. | 200 OK |
| PATCH | `/api/restaurantes/{id}/desactivar` | Desactivar un restaurante. | 204 No Content |

**Consideraciones:**
- Los endpoints de creación y actualización reciben un objeto `RestauranteRequestDTO`.
- Los datos recibidos se validan mediante `@Valid`.
- Las respuestas de creación y consulta utilizan `RestauranteResponseDTO`.
- La consulta general lista únicamente restaurantes activos.
- La desactivación no devuelve contenido en la respuesta.
- Los errores de validación y las excepciones de negocio se gestionan mediante el manejo de excepciones del microservicio.

### 1.2. Categorías

**Controlador:** `CategoriaController`  
**Ruta base:** `/api/restaurantes/{restauranteId}/categorias`

Este controlador administra las categorías pertenecientes a cada restaurante.

| Método HTTP | Endpoint | Descripción | Respuesta esperada |
|---|---|---|---|
| POST | `/api/restaurantes/{restauranteId}/categorias` | Crear una categoría para un restaurante. | 201 Created |
| GET | `/api/restaurantes/{restauranteId}/categorias` | Listar las categorías de un restaurante. | 200 OK |
| PUT | `/api/restaurantes/{restauranteId}/categorias/{categoriaId}` | Actualizar una categoría específica. | 200 OK |
| PATCH | `/api/restaurantes/{restauranteId}/categorias/{categoriaId}/desactivar` | Desactivar una categoría. | 204 No Content |

**Consideraciones:**
- La creación y actualización reciben un objeto `CategoriaRequestDTO`.
- Las respuestas de creación, consulta y actualización utilizan `CategoriaResponseDTO`.
- Las operaciones requieren el identificador del restaurante.
- Para actualizar o desactivar una categoría también se requiere su identificador (`categoriaId`).
- Los datos recibidos se validan mediante `@Valid`.
- La desactivación no devuelve contenido en la respuesta.
### 1.3. Platos

**Controlador:** `PlatoController`  
**Ruta base:** `/api/platos`

Este controlador administra los platos asociados a las categorías de los restaurantes.

| Método HTTP | Endpoint | Descripción | Respuesta esperada |
|---|---|---|---|
| POST | `/api/platos` | Registrar un plato. | 201 Created |
| GET | `/api/platos/categoria/{categoriaId}` | Consultar los platos de una categoría. | 200 OK |
| GET | `/api/platos/{id}` | Consultar un plato por su ID. | 200 OK |
| PUT | `/api/platos/{id}` | Actualizar un plato. | 200 OK |
| PATCH | `/api/platos/{id}/disponibilidad?disponible={valor}` | Cambiar la disponibilidad de un plato. | 200 OK |

**Consideraciones:**
- La creación y actualización reciben un objeto `PlatoRequestDTO`.
- Las respuestas utilizan `PlatoResponseDTO`.
- Para crear y actualizar se aplica validación mediante `@Valid`.
- El cambio de disponibilidad utiliza el parámetro de consulta `disponible`, cuyo valor es booleano (`true` o `false`).
- Los platos se consultan por categoría o por su identificador.
### 1.4. Extras

**Controlador:** `ExtraController`  
**Ruta base:** `/api/restaurantes/{restauranteId}/extras`

Este controlador administra los extras que pueden asociarse a los platos de un restaurante.

| Método HTTP | Endpoint | Descripción | Respuesta esperada |
|---|---|---|---|
| POST | `/api/restaurantes/{restauranteId}/extras` | Registrar un extra para un restaurante. | 201 Created |
| GET | `/api/restaurantes/{restauranteId}/extras` | Listar los extras de un restaurante. | 200 OK |
| GET | `/api/restaurantes/{restauranteId}/extras/{id}` | Consultar un extra específico. | 200 OK |
| PUT | `/api/restaurantes/{restauranteId}/extras/{id}` | Actualizar un extra. | 200 OK |
| PATCH | `/api/restaurantes/{restauranteId}/extras/{id}/disponibilidad?disponible={valor}` | Cambiar la disponibilidad de un extra. | 200 OK |

**Consideraciones:**
- La creación y actualización reciben un objeto `ExtraRequestDTO`.
- Las respuestas utilizan `ExtraResponseDTO`.
- Los datos recibidos para creación y actualización se validan mediante `@Valid`.
- El cambio de disponibilidad recibe el parámetro booleano `disponible`.
- Todas las operaciones requieren el identificador del restaurante.
- Los extras se administran dentro del contexto del restaurante al que pertenecen.
### 1.5. Horarios de restaurantes

**Controlador:** `HorarioRestauranteController`  
**Ruta base:** `/api/restaurantes/{restauranteId}/horarios`

Este controlador administra los horarios de atención de cada restaurante.

| Método HTTP | Endpoint | Descripción | Respuesta esperada |
|---|---|---|---|
| POST | `/api/restaurantes/{restauranteId}/horarios` | Registrar un horario para un restaurante. | 201 Created |
| GET | `/api/restaurantes/{restauranteId}/horarios` | Consultar los horarios de un restaurante. | 200 OK |
| PUT | `/api/restaurantes/{restauranteId}/horarios/{horarioId}` | Actualizar un horario existente. | 200 OK |
| DELETE | `/api/restaurantes/{restauranteId}/horarios/{horarioId}` | Eliminar un horario. | 204 No Content |

**Consideraciones:**
- La creación y actualización reciben un objeto `HorarioRequestDTO`.
- Las respuestas utilizan `HorarioResponseDTO`.
- Los datos recibidos se validan mediante `@Valid`.
- Todas las operaciones requieren el identificador del restaurante.
- Para actualizar o eliminar un horario también se requiere su identificador.
- La eliminación no devuelve contenido en la respuesta.
### 1.6. Menú completo

**Controlador:** `MenuController`  
**Ruta base:** `/api/restaurantes/{restauranteId}/menu`

Este controlador permite consultar la información completa del menú de un restaurante mediante una única solicitud.

| Método HTTP | Endpoint | Descripción | Respuesta esperada |
|---|---|---|---|
| GET | `/api/restaurantes/{restauranteId}/menu` | Consultar el menú completo de un restaurante. | 200 OK |

**Consideraciones:**
- Requiere el identificador del restaurante.
- La respuesta utiliza `MenuResponseDTO`.
- Incluye la información del restaurante, sus categorías, los platos de cada categoría y los extras asociados a cada plato.
- El servicio filtra los restaurantes, categorías, platos y extras que no se encuentren disponibles según sus reglas de negocio.
- Este endpoint facilita la integración con otros microservicios, especialmente Pedidos, al proporcionar la información del menú en una sola consulta.
### 1.7. Asociación de platos y extras

**Controlador:** `PlatoExtraController`  
**Ruta base:** `/api/platos/{platoId}/extras`

Este controlador administra la relación entre los platos y los extras que pueden seleccionarse para cada uno.

| Método HTTP | Endpoint | Descripción | Respuesta esperada |
|---|---|---|---|
| POST | `/api/platos/{platoId}/extras/{extraId}` | Asociar un extra a un plato. | 201 Created |
| GET | `/api/platos/{platoId}/extras` | Consultar los extras asociados a un plato. | 200 OK |
| DELETE | `/api/platos/{platoId}/extras/{extraId}` | Quitar la asociación entre un plato y un extra. | 204 No Content |

**Consideraciones:**
- Para las operaciones se requieren los identificadores del plato y del extra, según corresponda.
- La respuesta de creación y consulta utiliza `PlatoExtraResponseDTO`.
- La asociación permite establecer qué extras están disponibles para cada plato.
- Eliminar una asociación no implica eliminar el registro del extra ni del plato.
## 2. Microservicio Pedidos

**Puerto:** 8082  
**Base de datos:** `foodexpress_pedidos`  
**Base URL:** `/api/pedidos`

Este microservicio permite crear pedidos asociados a un restaurante, registrar los platos seleccionados, sus extras y notas, calcular los valores correspondientes y consultar la información de los pedidos existentes.

### 2.1 Crear pedido

**Método:** `POST`  
**Endpoint:** `/api/pedidos`

**Descripción:** Crea un pedido para un cliente en un restaurante. Permite incluir uno o varios platos, cantidades, extras y notas individuales.

**Cuerpo de la solicitud (JSON):**

```json
{
  "clienteId": 1,
  "restauranteId": 1,
  "items": [
    {
      "platoId": 1,
      "cantidad": 2,
      "extrasIds": [1],
      "notas": "Sin cebolla"
    },
    {
      "platoId": 2,
      "cantidad": 1,
      "extrasIds": [],
      "notas": "Bien cocido"
    }
  ]
}
```

**Consideraciones:**
- El pedido debe pertenecer a un único cliente y restaurante.
- Debe contener al menos un plato.
- La cantidad de cada plato debe ser mayor que cero.
- Los platos y extras deben existir, estar disponibles y corresponder al restaurante y plato seleccionados.
- El cliente no envía precios ni totales. El servidor consulta los precios del menú y realiza los cálculos.
- Los nombres y precios se guardan como datos históricos del pedido.

**Respuesta esperada:** `201 Created`

Ejemplo de respuesta:

```json
{
  "id": 1,
  "clienteId": 1,
  "restauranteId": 1,
  "fechaCreacion": "2026-11-14T12:00:00",
  "estado": "RECIBIDO",
  "items": [
    {
      "id": 1,
      "platoId": 1,
      "nombrePlato": "Hamburguesa clásica",
      "precioUnitario": 25000.00,
      "cantidad": 2,
      "notas": "Sin cebolla",
      "extras": [
        {
          "extraId": 1,
          "nombreExtra": "Queso cheddar",
          "precioAdicional": 3000.00
        }
      ],
      "subtotal": 56000.00
    }
  ],
  "subtotal": 56000.00,
  "impuestos": 0.00,
  "total": 56000.00
}
```

*Nota:* Los valores de impuestos se manejarán mediante una configuración acordada por el equipo. El ejemplo utiliza 0.00 de manera provisional, no establece una tarifa definitiva.

### 2.2 Consultar pedido por ID

**Método:** `GET`  
**Endpoint:** `/api/pedidos/{id}`

**Descripción:** Consulta la información de un pedido específico, incluyendo los identificadores del cliente y restaurante, los platos, extras, cantidades, notas, estado y valores calculados.

**Parámetros de ruta:**
- `id`: identificador del pedido que se desea consultar.

**Respuesta esperada:** `200 OK`

Ejemplo: devuelve la estructura de respuesta presentada en el endpoint de creación, con la información almacenada del pedido.

### 2.3 Operaciones de estado y cancelación

Estas operaciones se incluyen como referencia para la integración del equipo. Su implementación corresponde al integrante encargado del patrón State y las transiciones de estado.

| Método | Endpoint | Descripción |
|---|---|---|
| PATCH | `/api/pedidos/{id}/estado` | Cambiar el estado del pedido según las transiciones permitidas. |
| POST | `/api/pedidos/{id}/cancelar` | Solicitar la cancelación de un pedido. |

Los cuerpos de estas solicitudes y las reglas definitivas deberán acordarse con el responsable del patrón State.

### 2.4 Códigos de respuesta esperados

| Código HTTP | Significado |
|---|---|
| 200 OK | Consulta realizada correctamente. |
| 201 Created | Pedido creado correctamente. |
| 400 Bad Request | Datos inválidos, campos obligatorios faltantes, cantidades incorrectas o incumplimiento de validaciones del pedido. |
| 404 Not Found | Pedido solicitado no encontrado. |
| 502 Bad Gateway | Error al comunicarse con el microservicio externo Restaurantes y Menú. |
| 500 Internal Server Error | Error interno no controlado del servidor. |

*Nota:* Estos códigos representan el comportamiento esperado del contrato. Se confirmarán durante la implementación y las pruebas.