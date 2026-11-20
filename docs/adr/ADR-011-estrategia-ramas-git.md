# ADR-011: Estrategia de ramas y colaboración con Git

- **Fecha de la decisión:** 2026-11-19
- **Estado:** Aceptado
- **Evidencia:** el Pull Request #1 (`front-end-branch` → `main`, fusionado el 2026-11-18) fue el primer uso de ramas y PR; esta estrategia se formaliza para el trabajo restante.
- **Registrado en el repositorio:** 2026-11-19

## Contexto
El backend se desarrolló directamente sobre `main` y el frontend en `front-end-branch`, sin revisión entre pares hasta el PR #1. El equipo (3 integrantes) necesita una estrategia simple para integrar el trabajo restante con revisión, y el plazo de entrega es corto.

## Decisión
Adoptamos GitHub Flow: `main` siempre estable, ramas cortas por tarea (`feature/…`, `fix/…`, `docs/…`, `chore/…`) integradas mediante Pull Request con al menos una aprobación de otro integrante, y commits con Conventional Commits. Los detalles están en `docs/git/estrategia-ramas.md`.

## Opciones evaluadas
- **GitFlow:** `develop`, `release` y `hotfix` añaden ceremonia que no se justifica sin versiones paralelas ni releases programados.
- **GitHub Flow (elegida):** una rama estable y ramas temporales; encaja con un equipo pequeño y entregas continuas.
- **Trunk-based:** exige integración muy frecuente y pruebas automáticas (CI) que el proyecto aún no tiene.

## Consecuencias
- (+) Flujo simple, revisión antes de integrar e historial legible.
- (−) Requiere disciplina para no hacer commits directos a `main`; sin CI, la estabilidad depende de la revisión humana.
- (−) La mayor parte del historial anterior (commits del 14 al 18 de noviembre) no siguió esta estrategia; se documenta en B.5.2.
