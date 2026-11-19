
package com.foodexpress.pedidos.service;

import com.foodexpress.pedidos.dto.menu.MenuExtraDTO;
import com.foodexpress.pedidos.dto.menu.MenuPlatoDTO;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PedidoCalculoService {

    // Calcular el subtotal de un plato con sus extras
    public BigDecimal calcularSubtotalItem(
            MenuPlatoDTO plato,
            List<MenuExtraDTO> extras,
            Integer cantidad) {

        BigDecimal precioUnitario = plato.getPrecio();

        // Sumar el precio de los extras
        for (MenuExtraDTO extra : extras) {
            precioUnitario = precioUnitario.add(
                    extra.getPrecioAdicional()
            );
        }

        // Multiplicar el precio por la cantidad
        return precioUnitario
                .multiply(BigDecimal.valueOf(cantidad))
                .setScale(2, RoundingMode.HALF_UP);
    }

    // Sumar los subtotales de todos los platos
    public BigDecimal calcularSubtotalPedido(
            List<BigDecimal> subtotalesItems) {

        BigDecimal subtotal = BigDecimal.ZERO;

        for (BigDecimal valor : subtotalesItems) {
            subtotal = subtotal.add(valor);
        }

        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    // Calcular el importe de impuestos
    public BigDecimal calcularImpuestos(
            BigDecimal subtotal,
            BigDecimal tasaImpuesto) {

        return subtotal
                .multiply(tasaImpuesto)
                .setScale(2, RoundingMode.HALF_UP);
    }

    // Calcular el total del pedido
    public BigDecimal calcularTotal(
            BigDecimal subtotal,
            BigDecimal impuestos) {

        return subtotal.add(impuestos)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
