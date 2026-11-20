# FoodExpress

Sistema de pedidos de comida con microservicios (Spring Boot 4, Java 21, PostgreSQL, RabbitMQ).

| Componente | Puerto | Base de datos |
|---|---|---|
| frontend (React + nginx) | 3000 | — |
| restaurantes-menu | 8081 | foodexpress_restaurantes |
| pedidos | 8082 | foodexpress_pedidos |
| notificaciones | 8083 | foodexpress_notificaciones |

## Cómo correrlo

Requisito: Docker Desktop abierto.

```bash
cp .env.example .env          # solo la primera vez
docker compose up --build -d
docker compose ps             # esperar a que los 6 contenedores estén (healthy)
```

Abrir la web: **http://localhost:3000**

Datos de prueba opcionales (crea un restaurante abierto con menú y extras) y flujo completo:
```bash
./scripts/seed.sh
./scripts/probar-flujo.sh
```

| Servicio | URL |
|---|---|
| **Frontend (la web)** | http://localhost:3000 |
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
### Redes y puertos

El Compose usa tres redes propias, no la red por defecto de Docker:

| Red | Propósito | Miembros |
|---|---|---|
| `datos` | Servicios ↔ PostgreSQL | postgres, los 3 microservicios |
| `mensajeria` | Servicios ↔ RabbitMQ | rabbitmq, los 3 microservicios |
| `servicios` | HTTP entre contenedores | frontend, los 3 microservicios |

El frontend solo está en `servicios`: no puede llegar a la base de datos ni al broker. Detalle y alternativas en `docs/adr/ADR-017-redes-docker-compose.md`.

Puertos publicados: 3000 (web) y 8081–8083 (APIs, para pruebas). Los puertos 5432 (PostgreSQL), 5672 y 15672 (RabbitMQ) se publican **solo en `127.0.0.1`**, para inspeccionar datos y depurar desde el IDE sin abrirlos a la red. Justificación en `docs/adr/ADR-018-puertos-expuestos.md`.

La arquitectura, las decisiones y el proceso están en `docs/` (ADR en `docs/adr/`, Git en `docs/git/`, pruebas en `docs/pruebas/`, lecciones aprendidas en `docs/proceso/`).
## Documentación

- `docs/arquitectura/api.md`: endpoints de los 3 microservicios.
- `docs/arquitectura/mensajeria.md`: eventos de RabbitMQ, patrones, SOLID y atributos de calidad.
- `docs/arquitectura/modelo-datos.md`: modelo de datos.

## Frontend

Carpeta `frontend/` (React + Vite). En Docker lo sirve nginx, que además reenvía `/api/restaurantes` y `/api/platos` → 8081, `/api/pedidos` → 8082, `/api/notificaciones` y `/ws` → 8083 (así el navegador solo habla con el puerto 3000 y no hay CORS).

Los roles no tienen login: se eligen en la pantalla de inicio.

| Rol | Qué puede hacer |
|---|---|
| Cliente | Ver restaurantes y menús, pedir platos con extras y notas, ver y cancelar sus pedidos, recibir avisos en tiempo real. Se identifica con un número de cliente. |
| Empleado | Ver los pedidos de todos los clientes (tablero por estado), avanzarlos o cancelarlos. |
| Administrador | Crear/editar restaurantes, abrirlos o cerrarlos, horarios, categorías, platos (precio, stock, disponibilidad) y extras, y asociar extras a platos. |

Un restaurante nuevo nace **cerrado**: el administrador debe abrirlo para que reciba pedidos.

Desarrollo del frontend sin Docker (con el backend ya corriendo): `cd frontend && npm install && npm run dev` → http://localhost:3000
