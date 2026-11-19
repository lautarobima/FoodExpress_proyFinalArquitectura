# ADR-011: Estrategia de ramas y colaboración con Git

**Fecha:** 2026-11-18  
**Estado:** Aceptado (pendiente de confirmación del equipo)

## Contexto

Durante el desarrollo de FoodExpress, el backend se construyó directamente sobre la rama `main`. Posteriormente, el frontend se desarrolló en una rama independiente para separar sus cambios antes de integrarlos al proyecto.

Se requiere establecer una estrategia que facilite la colaboración, la revisión de cambios y la estabilidad del código.

## Decisión

Se adoptará GitHub Flow como estrategia de trabajo para las siguientes etapas del proyecto. La rama `main` contendrá la versión integrada y estable, mientras que las nuevas funcionalidades y correcciones se desarrollarán en ramas independientes y se incorporarán mediante Pull Requests.

## Opciones evaluadas

- **GitFlow:** utiliza ramas permanentes de desarrollo y ramas adicionales para versiones y correcciones.
- **GitHub Flow:** utiliza una rama principal y ramas temporales para funcionalidades, integradas mediante Pull Requests.
- **Trunk-Based Development:** promueve la integración frecuente de cambios directamente sobre una rama principal.

## Consecuencias

**Positivas:**
- Flujo de trabajo sencillo.
- Separación de funcionalidades.
- Revisión de código antes de integrar cambios.
- Menor complejidad de administración de ramas.

**Negativas:**
- Requiere disciplina para crear Pull Requests.
- La estabilidad de `main` depende de las revisiones y validaciones realizadas.