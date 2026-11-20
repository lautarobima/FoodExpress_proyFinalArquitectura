# ADR-017: Tres redes Docker segmentadas por tipo de tráfico

- **Fecha de la decisión:** 2026-11-18
- **Estado:** Aceptado
- **Evidencia:** commit `8aafdf4` (sección `networks` y asignación de redes en `docker-compose.yml`) y `421990c` (el frontend se une a `servicios`)
- **Registrado en el repositorio:** 2026-11-19

## Contexto
La guía pide no usar la red por defecto de Docker y definir redes propias. Los componentes se comunican por tres razones distintas: acceso a datos, mensajería y llamadas HTTP. El navegador solo debe llegar al frontend.

## Decisión
Definimos tres redes y asignamos a cada contenedor solo las que necesita:

| Red | Propósito | Miembros |
|---|---|---|
| `datos` | Servicios ↔ PostgreSQL | postgres, restaurantes-menu, pedidos, notificaciones |
| `mensajeria` | Servicios ↔ RabbitMQ | rabbitmq, restaurantes-menu, pedidos, notificaciones |
| `servicios` | HTTP entre contenedores | frontend (nginx), restaurantes-menu, pedidos, notificaciones |

## Opciones evaluadas
- **A. Red por defecto de Docker:** una sola red plana donde todo ve a todo; no cumple la guía.
- **B. Una red propia por servicio:** exigiría unir cada backend a varias redes y generaría seis o más redes con las mismas conexiones cruzadas; poco legible para seis componentes.
- **C. Una única red personalizada:** cumple la guía, pero el frontend podría llegar a PostgreSQL y RabbitMQ.
- **D. Tres redes por tipo de tráfico (elegida):** equilibrio entre aislamiento y simplicidad.

## Consecuencias
- (+) El frontend solo está en `servicios`: no puede conectarse a la base de datos ni al broker.
- (+) PostgreSQL solo está en `datos` y RabbitMQ solo en `mensajeria`, así que no se ven entre sí.
- (+) El nombre de cada red documenta para qué sirve.
- (−) Los tres backends pertenecen a las tres redes, por lo que entre ellos no hay aislamiento.
- (−) Interpretamos "red propia" como "redes explícitas y distintas de la de Docker por defecto", no como una red por contenedor.
