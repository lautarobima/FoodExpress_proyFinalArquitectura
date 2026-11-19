# ADR-011: Estrategia de ramas y colaboración con Git

- **Fecha:** 19-11-2026
- **Estado:** Aceptado

## Contexto
El backend se desarrolló inicialmente sobre `main` y el frontend en `front-end-branch`, sin Pull Requests. El equipo necesita una estrategia simple para integrar el trabajo restante con revisión entre pares, con 3 integrantes y un plazo corto.

## Decisión
Adoptamos GitHub Flow: `main` siempre estable, ramas cortas por funcionalidad (`feature/…`, `fix/…`, `docs/…`) integradas mediante Pull Request con aprobación de al menos otro integrante. Los commits siguen Conventional Commits.

## Opciones evaluadas
- **GitFlow:** `develop`, `release` y `hotfix` añaden ceremonia que no se justifica sin versiones paralelas.
- **GitHub Flow:** una rama estable y ramas cortas; encaja con equipo pequeño y entregas continuas.
- **Trunk-based:** exige pruebas automáticas y CI robustos que aún no tenemos.

## Consecuencias
(+) Flujo simple; revisión antes de integrar; historial legible.
(−) Requiere disciplina para no hacer commits directos a `main`; sin CI, la estabilidad depende de la revisión humana.
(−) Las primeras etapas del proyecto no siguieron esta estrategia (ver B.5.2).