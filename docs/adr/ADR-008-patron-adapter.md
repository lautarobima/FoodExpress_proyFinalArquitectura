# ADR-008: Patrón Adapter para los canales de notificación

- **Fecha de la decisión:** 2026-11-15
- **Estado:** Aceptado
- **Evidencia:** commit `cb08de8` (`adapter/CanalNotificacion.java`, `EmailCanalAdapter`, `PushCanalAdapter`, `WebSocketCanalAdapter`)
- **Registrado en el repositorio:** 2026-11-19

## Contexto
Cada canal tiene una API distinta: el servicio de correo expone `sendMail(to, subject, htmlBody)`, el de push `push(deviceToken, payload)` y WebSocket usa `SimpMessagingTemplate`. `NotificacionService` no debe conocer esos detalles.

## Decisión
Definimos la interfaz `CanalNotificacion` (`tipo()`, `enviar(MensajeNotificacion)`) y un adaptador por canal. Spring inyecta todos los adaptadores y `NotificacionService` los guarda en un mapa por `TipoCanal`.

## Opciones evaluadas
- **A. Lógica de cada canal dentro de `NotificacionService`:** el servicio crece y cambia con cada proveedor.
- **B. Strategy sin adaptar APIs externas:** sirve para variar algoritmos, pero no resuelve que las interfaces de los proveedores sean incompatibles.
- **C. Adapter (elegida):** adapta cada API existente a la interfaz común.

## Consecuencias
- (+) Un canal nuevo (SMS, WhatsApp) es una clase nueva, sin modificar el servicio (OCP).
- (+) Los fallos se registran por notificación (`EstadoEnvio.FALLIDA` y `detalleError`).
- (−) Correo y push son simulados (`ServicioCorreoSimulado`, `ServicioPushSimulado`); un proveedor real exigiría credenciales y manejo de errores propio.
