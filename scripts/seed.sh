#!/bin/bash
# Datos de prueba para FoodExpress
# Requiere restaurantes-menu (8081) y pedidos (8082) corriendo.

MENU=http://localhost:8081
PED=http://localhost:8082
H="Content-Type: application/json"

id() { grep -o '"id":[0-9]*' | head -1 | cut -d: -f2; }

echo "== Restaurante"
R=$(curl -s -X POST $MENU/api/restaurantes -H "$H" -d '{
  "nombre":"La Brasa Sabanera","descripcion":"Comida colombiana",
  "direccion":"Calle 1 # 2-3, Chía","telefono":"3001234567"}' | id)
echo "restauranteId=$R"

echo "== Horarios"
for D in LUNES MARTES MIERCOLES JUEVES VIERNES SABADO DOMINGO; do
  curl -s -o /dev/null -X POST $MENU/api/restaurantes/$R/horarios -H "$H" \
    -d "{\"diaSemana\":\"$D\",\"apertura\":\"08:00:00\",\"cierre\":\"22:00:00\"}"
done

echo "== Categorías"
C_FUERTE=$(curl -s -X POST $MENU/api/restaurantes/$R/categorias -H "$H" -d '{"nombre":"Platos fuertes"}' | id)
C_BEBIDA=$(curl -s -X POST $MENU/api/restaurantes/$R/categorias -H "$H" -d '{"nombre":"Bebidas"}' | id)
echo "fuertes=$C_FUERTE bebidas=$C_BEBIDA"

echo "== Platos"
P_HAMB=$(curl -s -X POST $MENU/api/platos -H "$H" -d "{\"categoriaId\":$C_FUERTE,\"nombre\":\"Hamburguesa\",\"descripcion\":\"Carne 150g\",\"precio\":25000}" | id)
P_BANDEJA=$(curl -s -X POST $MENU/api/platos -H "$H" -d "{\"categoriaId\":$C_FUERTE,\"nombre\":\"Bandeja paisa\",\"precio\":32000}" | id)
P_LIMO=$(curl -s -X POST $MENU/api/platos -H "$H" -d "{\"categoriaId\":$C_BEBIDA,\"nombre\":\"Limonada de coco\",\"precio\":9000}" | id)
echo "hamburguesa=$P_HAMB bandeja=$P_BANDEJA limonada=$P_LIMO"

echo "== Extras"
E_QUESO=$(curl -s -X POST $MENU/api/restaurantes/$R/extras -H "$H" -d '{"nombre":"Queso extra","tipo":"ADICION","precioAdicional":3000}' | id)
E_TOCINETA=$(curl -s -X POST $MENU/api/restaurantes/$R/extras -H "$H" -d '{"nombre":"Tocineta","tipo":"ADICION","precioAdicional":4000}' | id)
E_SINCEB=$(curl -s -X POST $MENU/api/restaurantes/$R/extras -H "$H" -d '{"nombre":"Sin cebolla","tipo":"MODIFICACION","precioAdicional":0}' | id)
echo "queso=$E_QUESO tocineta=$E_TOCINETA sinCebolla=$E_SINCEB"

echo "== Asociar extras a la hamburguesa"
for E in $E_QUESO $E_TOCINETA $E_SINCEB; do
  curl -s -o /dev/null -X POST $MENU/api/platos/$P_HAMB/extras/$E
done

echo "== Menú completo"
curl -s $MENU/api/restaurantes/$R/menu; echo

echo "== Abrir el restaurante (solo los abiertos reciben pedidos)"
curl -s -o /dev/null -X PATCH "$MENU/api/restaurantes/$R/estado-operativo?estado=ABIERTO"

echo "== Pedido de prueba"
curl -s -X POST $PED/api/pedidos -H "$H" -d "{
  \"clienteId\":1,\"restauranteId\":$R,
  \"items\":[
    {\"platoId\":$P_HAMB,\"cantidad\":2,\"extrasIds\":[$E_QUESO,$E_SINCEB],\"notas\":\"Término medio\"},
    {\"platoId\":$P_LIMO,\"cantidad\":1}
  ]}"
echo
echo "Esperado: subtotal 65000, impuestos 5200 (8%), total 70200"
