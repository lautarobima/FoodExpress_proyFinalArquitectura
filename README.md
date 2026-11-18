# FoodExpress

Sistema de pedidos de comida con microservicios (Spring Boot 4, Java 21, PostgreSQL, RabbitMQ).

| Microservicio | Puerto | Base de datos |
|---|---|---|
| restaurantes-menu | 8081 | foodexpress_restaurantes |
| pedidos | 8082 | foodexpress_pedidos |
| notificaciones | 8083 | foodexpress_notificaciones |

## Cómo correrlo

Requisito: Docker Desktop abierto.

```bash
cp .env.example .env          # solo la primera vez
docker compose up --build -d
docker compose ps             # esperar a que los 5 contenedores estén (healthy)
```

Datos de prueba y flujo completo:
```bash
./scripts/seed.sh
./scripts/probar-flujo.sh
```

| Servicio | URL |
|---|---|
| Restaurantes y Menú | http://localhost:8081 |
| Pedidos | http://localhost:8082 |
| Notificaciones (API + WebSocket `/ws`) | http://localhost:8083 |
| Panel de notificaciones en tiempo real | http://localhost:8083/panel.html |
| Panel de RabbitMQ | http://localhost:15672 (usuario y clave del `.env`) |
| Health check de cada servicio | `/actuator/health` |

Apagar: `docker compose down` (los datos se conservan). Para borrar también los datos: `docker compose down -v`.

### Variables de entorno

Definidas en `.env` (plantilla en `.env.example`; el `.env` real no se sube al repositorio):

| Variable | Uso |
|---|---|
| `DB_USER`, `DB_PASSWORD` | Credenciales de PostgreSQL |
| `RABBITMQ_USER`, `RABBITMQ_PASSWORD` | Credenciales de RabbitMQ |
| `PEDIDOS_IMPUESTO_TASA` | Tasa de impuesto de Pedidos (por defecto 0.08) |

### Desarrollo (un servicio desde el IDE)

Para depurar un servicio sin reconstruir su imagen: detén su contenedor y córrelo local. Usa la BD y RabbitMQ de Docker por `localhost`.
```bash
docker compose stop pedidos
cd backend/pedidos
export DB_PASSWORD=postgres
./mvnw spring-boot:run
```

## Documentación

- `docs/arquitectura/api.md`: endpoints de los 3 microservicios.
- `docs/arquitectura/mensajeria.md`: eventos de RabbitMQ, patrones, SOLID y atributos de calidad.
- `docs/arquitectura/modelo-datos.md`: modelo de datos.
