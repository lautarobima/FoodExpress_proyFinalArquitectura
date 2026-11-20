# ADR-015: Sin autenticación en esta entrega (roles seleccionados en la interfaz)

- **Fecha de la decisión:** 2026-11-18
- **Estado:** Aceptado (deuda técnica declarada)
- **Evidencia:** commit `421990c` (frontend: `session.jsx` y pantalla de inicio donde se elige el rol); README: "los roles no tienen login"
- **Registrado en el repositorio:** 2026-11-19

## Contexto
El sistema distingue tres perfiles (cliente, empleado, administrador). La autenticación JWT con roles figura como bonificación opcional en el catálogo, y el foco del curso es la arquitectura, los patrones y la mensajería. Había poco tiempo para la entrega final.

## Decisión
No implementamos autenticación ni autorización. El perfil se elige en la pantalla de inicio del frontend y el cliente se identifica con un número de cliente (`clienteId`).

## Opciones evaluadas
- **A. JWT con Spring Security y roles:** estándar para APIs REST sin estado; requiere un servicio o módulo de usuarios, almacenamiento de credenciales y protección de cada endpoint.
- **B. Sesiones con cookie:** más simple, pero contradice la naturaleza sin estado de los servicios y complica el proxy y el WebSocket.
- **C. OAuth2 / Keycloak:** solución completa, pero agrega otro contenedor y bastante configuración.
- **D. Sin autenticación (elegida):** permite demostrar el flujo completo dentro del plazo.

## Consecuencias
- (+) Se prioriza el flujo de pedidos, los patrones y la mensajería.
- (−) Cualquier persona con acceso a la red puede llamar a cualquier endpoint (por ejemplo, cambiar estados o editar el menú) y hacerse pasar por otro cliente.
- (−) Por eso los puertos se publican solo para desarrollo (ADR-018).
- (−) Mejora futura: JWT con roles en un API Gateway delante de los servicios.
