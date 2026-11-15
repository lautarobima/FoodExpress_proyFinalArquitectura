# FoodExpress

Sistema de pedidos de comida con microservicios (Spring Boot 4, Java 21, PostgreSQL, RabbitMQ).

| Microservicio | Puerto | Base de datos |
|---|---|---|
| restaurantes-menu | 8081 | foodexpress_restaurantes |
| pedidos | 8082 | foodexpress_pedidos |
| notificaciones | 8083 | foodexpress_notificaciones |

## Cómo correrlo

1. Infraestructura (PostgreSQL con las 3 bases + RabbitMQ):
   ```bash
   docker compose up -d
   ```
   Si ya tienes otro PostgreSQL en el puerto 5432 con las bases creadas, levanta solo RabbitMQ:
   `docker compose up -d rabbitmq`

2. Cada microservicio en su propia terminal (primero restaurantes-menu):
   ```bash
   cd backend/restaurantes-menu
   export DB_PASSWORD=postgres
   ./mvnw spring-boot:run
   ```

3. Datos de prueba y flujo completo:
   ```bash
   ./scripts/seed.sh
   ./scripts/probar-flujo.sh
   ```

- Panel de RabbitMQ: http://localhost:15672 (guest / guest)
- Notificaciones en tiempo real: http://localhost:8083/panel.html

## Documentación

- `docs/arquitectura/api.md`: endpoints de los 3 microservicios.
- `docs/arquitectura/mensajeria.md`: eventos de RabbitMQ, patrones, SOLID y atributos de calidad.
- `docs/arquitectura/modelo-datos.md`: modelo de datos.
