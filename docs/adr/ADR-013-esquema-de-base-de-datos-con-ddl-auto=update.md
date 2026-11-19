# ADR-013: Esquema de base de datos con `ddl-auto=update` (sin Flyway)

- **Fecha:** 19-11-2026  |  **Estado:** Aceptado 

## Contexto
El modelo cambió con frecuencia durante el desarrollo y el equipo tenía poco tiempo.

## Decisión
Hibernate genera y actualiza el esquema (`spring.jpa.hibernate.ddl-auto=update`).

## Opciones evaluadas
- **Flyway / Liquibase:** migraciones versionadas y reproducibles; mayor esfuerzo inicial.
- **Scripts SQL manuales:** control total, pero propenso a desincronizarse del modelo.
- **ddl-auto=update:** cero configuración, cambios inmediatos.

## Consecuencias
(+) Iteración rápida. (−) No hay historial de cambios ni control de migraciones; `update` no elimina columnas ni garantiza cambios seguros en producción. Se registra como deuda técnica.