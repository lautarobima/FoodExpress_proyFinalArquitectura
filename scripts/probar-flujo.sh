#!/bin/bash
# Prueba el flujo completo: estados, cancelación, restaurante cerrado y notificaciones.
# Requiere los 3 microservicios corriendo y haber ejecutado seed.sh.
# Uso: ./scripts/probar-flujo.sh [restauranteId] [platoId] [extraId]

R=${1:-1}
PLATO=${2:-1}
EXTRA=${3:-1}

MENU=http://localhost:8081
PED=http://localhost:8082
NOT=http://localhost:8083
H="Content-Type: application/json"

id()     { grep -o '"id":[0-9]*' | head -1 | cut -d: -f2; }
estado() { grep -o '"estado":"[A-Z_]*"' | head -1 | cut -d'"' -f4; }

crear_pedido() {
  curl -s -X POST $PED/api/pedidos -H "$H" -d "{
    \"clienteId\":1,\"restauranteId\":$R,
    \"items\":[{\"platoId\":$PLATO,\"cantidad\":1,\"extrasIds\":[$EXTRA]}]}"
}

echo "== 1. Pedido A: flujo completo"
A=$(crear_pedido | id)
echo "Pedido A = $A"
for i in 1 2 3; do
  echo "  avanzar -> $(curl -s -X PATCH $PED/api/pedidos/$A/estado | estado)"
done

echo "== 2. Cancelar un pedido ENTREGADO (esperado: 409)"
curl -s -o /dev/null -w "  HTTP %{http_code}\n" -X POST $PED/api/pedidos/$A/cancelar \
  -H "$H" -d '{"canceladoPor":"CLIENTE","motivo":"Prueba"}'

echo "== 3. Pedido B: el restaurante lo cancela"
B=$(crear_pedido | id)
echo "Pedido B = $B"
echo "  avanzar -> $(curl -s -X PATCH $PED/api/pedidos/$B/estado | estado)"
echo "  cancelar -> $(curl -s -X POST $PED/api/pedidos/$B/cancelar -H "$H" \
  -d '{"canceladoPor":"RESTAURANTE","motivo":"Se acabó el pan"}' | estado)"

echo "== 4. Restaurante cerrado (esperado: 422)"
curl -s -o /dev/null -X PATCH "$MENU/api/restaurantes/$R/estado-operativo?estado=CERRADO"
curl -s -o /dev/null -w "  HTTP %{http_code}\n" -X POST $PED/api/pedidos -H "$H" -d "{
  \"clienteId\":1,\"restauranteId\":$R,\"items\":[{\"platoId\":$PLATO,\"cantidad\":1}]}"
curl -s -o /dev/null -X PATCH "$MENU/api/restaurantes/$R/estado-operativo?estado=ABIERTO"
echo "  (restaurante abierto de nuevo)"

echo "== 5. Historial del cliente 1"
curl -s "$PED/api/pedidos?clienteId=1" | grep -o '"clienteId"' | wc -l | xargs echo "  pedidos:"

sleep 2
echo "== 6. Notificaciones del pedido A"
curl -s "$NOT/api/notificaciones?pedidoId=$A"; echo
echo "== 7. Notificaciones del pedido B"
curl -s "$NOT/api/notificaciones?pedidoId=$B"; echo
