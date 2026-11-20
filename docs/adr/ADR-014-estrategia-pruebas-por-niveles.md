# ADR-014: Estrategia de pruebas por niveles

- **Fecha de la decisión:** 2026-11-18
- **Estado:** Aceptado
- **Evidencia:** commit `8aafdf4` (Dockerfiles con `mvn package -DskipTests`); pruebas unitarias en `backend/pedidos/src/test`
- **Registrado en el repositorio:** 2026-11-19

## Contexto
Los tests `*ApplicationTests` levantan el contexto completo de Spring y necesitan PostgreSQL y RabbitMQ, que no existen durante `docker build`. Al mismo tiempo, la lógica de estados y de cálculo de totales debe estar protegida.

## Decisión
- Pruebas unitarias sin infraestructura para la lógica crítica (`EstadoPedidoStateTest`, `PedidoCalculoServiceTest`).
- Pruebas funcionales y de integración manuales con Postman y verificación en PostgreSQL y RabbitMQ (ver `docs/pruebas/plan-pruebas.md`).
- Las imágenes se construyen con `-DskipTests`.

## Opciones evaluadas
- **A. Testcontainers para integración automática:** lo ideal, pero con alto costo de configuración para el plazo.
- **B. Solo pruebas manuales:** insuficiente para proteger reglas de dominio.
- **C. Unitarias automáticas más funcionales manuales (elegida).**

## Consecuencias
- (+) Las transiciones de estado y los cálculos quedan protegidos por pruebas automáticas.
- (−) La integración no se ejecuta automáticamente y no hay pruebas unitarias en Restaurantes y Menú ni en Notificaciones.
- (−) `-DskipTests` en el build implica que una prueba rota no detiene la construcción de la imagen.
