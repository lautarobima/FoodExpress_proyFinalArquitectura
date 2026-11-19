# ADR-014: Estrategia de pruebas por niveles

- **Fecha:** 19-11-2026  |  **Estado:** Aceptado

## Contexto
Los tests que levantan el contexto de Spring requieren PostgreSQL y RabbitMQ, que no existen durante `docker build`.

## Decisión
- Pruebas **unitarias** sin infraestructura para la lógica crítica (`EstadoPedidoStateTest`, `PedidoCalculoServiceTest`).
- Pruebas **funcionales e de integración** manuales con Postman y verificación en PostgreSQL/RabbitMQ (plan de pruebas: MEN, NOT, INT, CAL).
- Las imágenes se construyen con `-DskipTests`.

## Opciones evaluadas
- Testcontainers para integración automática: ideal, pero costo de configuración alto para el plazo.
- Solo pruebas manuales: insuficiente para la lógica de dominio.
- Mezcla de unitarias automáticas y funcionales manuales.

## Consecuencias
(+) La lógica de estados y cálculos queda protegida. (−) La integración no se ejecuta automáticamente; baja cobertura fuera de Pedidos.