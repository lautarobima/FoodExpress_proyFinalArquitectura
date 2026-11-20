# ADR-003: PostgreSQL con una base de datos por microservicio

- **Fecha de la decisión:** 2026-11-14
- **Estado:** Aceptado
- **Evidencia:** commit `e470f58` (URLs JDBC distintas por servicio) y `cb08de8` (`docker/postgres/init.sql` crea las tres bases)
- **Registrado en el repositorio:** 2026-11-19

## Contexto
Pedidos, ítems y extras tienen relaciones e integridad fuertes, y los totales usan `BigDecimal`. Ningún servicio debe leer las tablas de otro, para poder cambiar su modelo sin romper a los demás.

## Decisión
Usamos PostgreSQL 16 en una sola instancia con tres bases separadas: `foodexpress_restaurantes`, `foodexpress_pedidos` y `foodexpress_notificaciones`. Cada servicio accede solo a la suya.

## Opciones evaluadas
- **A. Una base compartida por todos los servicios:** simple, pero acopla los esquemas y permite JOINs entre servicios.
- **B. Una base por servicio en una instancia (elegida):** independencia lógica con un solo contenedor.
- **C. Una instancia de PostgreSQL por servicio:** aislamiento total, pero tres contenedores más y más consumo de recursos para un entorno de curso.
- **D. MongoDB (por ejemplo para Notificaciones):** flexible para documentos, pero introduce un segundo motor sin una necesidad real.
- **Motor:** PostgreSQL sobre MySQL por su manejo estricto de tipos y su soporte estándar de SQL; no se encontró una necesidad que justificara otro motor.

## Consecuencias
- (+) Independencia de esquemas y sin relaciones JPA entre servicios (se guardan solo ids como `platoId`, `restauranteId`).
- (+) Un solo volumen y una sola imagen para respaldar.
- (−) La instancia es un punto único de falla.
- (−) Los tres servicios comparten usuario y contraseña (`DB_USER`/`DB_PASSWORD`); no hay credenciales por servicio.
- (−) Los datos de un servicio que se necesitan en otro (nombre del plato, precio) se copian al pedido al momento de crearlo.
