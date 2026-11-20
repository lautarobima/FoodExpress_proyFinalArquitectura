# ADR-001: Arquitectura basada en microservicios

- **Fecha de la decisión:** 2026-11-14
- **Estado:** Aceptado
- **Evidencia:** commit `e470f58` (estructura inicial con `backend/pedidos`, `backend/restaurantes-menu`, `backend/notificaciones`)
- **Registrado en el repositorio:** 2026-11-19

## Contexto
El catálogo del curso (Proyecto 7) exige descomponer FoodExpress en tres microservicios: Restaurantes y Menú, Pedidos y Notificaciones. Además, el atributo de calidad "disponibilidad" pide que los restaurantes reciban pedidos aunque Notificaciones falle. El equipo es de 3 personas y tenía una semana para la primera versión funcional.

## Decisión
Implementamos tres microservicios Spring Boot independientes, cada uno con su base de datos y su propio contenedor, que se comunican por REST (consulta de menú) y por eventos (RabbitMQ).

## Opciones evaluadas
- **A. Monolito en capas:** una sola aplicación y un solo despliegue; la menor complejidad. Descartada porque no permite demostrar el desacoplamiento Pedidos/Notificaciones ni escalar servicios por separado, y el catálogo exige microservicios.
- **B. Monolito modular:** módulos con fronteras claras dentro de un solo proceso. Es un buen punto medio, pero una falla de notificaciones seguiría compartiendo proceso con Pedidos.
- **C. Microservicios (elegida):** aislamiento de fallos, despliegue independiente y límites de dominio explícitos.

## Consecuencias
- (+) Notificaciones puede estar caído sin impedir que se creen pedidos.
- (+) Cada servicio evoluciona y se prueba por separado.
- (−) Más infraestructura (6 contenedores), configuración y monitoreo.
- (−) Consistencia eventual entre servicios (ej. el stock se ajusta por evento).
- (−) Los records de eventos se duplican en cada servicio para no compartir código.
