# ADR-013: Esquema de base de datos generado por Hibernate (`ddl-auto=update`)

- **Fecha de la decisión:** 2026-11-14
- **Estado:** Aceptado (deuda técnica declarada)
- **Evidencia:** commit `e470f58` (`spring.jpa.hibernate.ddl-auto=update` en los tres `application.properties`)
- **Registrado en el repositorio:** 2026-11-19

## Contexto
El modelo de datos cambió con frecuencia durante el desarrollo y el equipo tenía poco tiempo. Cada servicio es dueño de su esquema (ADR-003).

## Decisión
Hibernate crea y actualiza las tablas al arrancar (`spring.jpa.hibernate.ddl-auto=update`), sin herramienta de migraciones.

## Opciones evaluadas
- **A. Flyway o Liquibase:** migraciones versionadas y reproducibles; mayor esfuerzo inicial y una dependencia más por servicio.
- **B. Scripts SQL manuales:** control total, pero se desincronizan fácilmente del modelo.
- **C. `ddl-auto=update` (elegida):** cero configuración y cambios inmediatos.

## Consecuencias
- (+) Iteración rápida: un cambio en la entidad se refleja al reiniciar.
- (−) No hay historial de cambios de esquema; `update` no elimina columnas ni renombra de forma segura.
- (−) No es adecuado para producción. Mejora futura: Flyway con una migración base por servicio.
