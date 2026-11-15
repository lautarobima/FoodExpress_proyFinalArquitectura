package com.foodexpress.pedidos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import com.foodexpress.pedidos.dto.menu.MenuExtraDTO;
import com.foodexpress.pedidos.dto.menu.MenuPlatoDTO;

import org.junit.jupiter.api.Test;

// El cálculo de totales debe ser exacto (atributo de calidad: consistencia)
class PedidoCalculoServiceTest {

    private final PedidoCalculoService calculo = new PedidoCalculoService();

    private MenuPlatoDTO plato(String precio) {
        MenuPlatoDTO plato = new MenuPlatoDTO();
        plato.setPrecio(new BigDecimal(precio));
        return plato;
    }

    private MenuExtraDTO extra(String precio) {
        MenuExtraDTO extra = new MenuExtraDTO();
        extra.setPrecioAdicional(new BigDecimal(precio));
        return extra;
    }

    @Test
    void subtotalItemSumaExtrasYMultiplicaPorCantidad() {
        // 2 x (25000 + 3000 + 0) = 56000
        BigDecimal subtotal = calculo.calcularSubtotalItem(
                plato("25000"), List.of(extra("3000"), extra("0")), 2);

        assertEquals(new BigDecimal("56000.00"), subtotal);
    }

    @Test
    void totalConImpuestoDel8Porciento() {
        BigDecimal subtotal = calculo.calcularSubtotalPedido(
                List.of(new BigDecimal("56000.00"), new BigDecimal("9000.00")));
        BigDecimal impuestos = calculo.calcularImpuestos(
                subtotal, new BigDecimal("0.08"));
        BigDecimal total = calculo.calcularTotal(subtotal, impuestos);

        assertEquals(new BigDecimal("65000.00"), subtotal);
        assertEquals(new BigDecimal("5200.00"), impuestos);
        assertEquals(new BigDecimal("70200.00"), total);
    }

    @Test
    void redondeoHalfUpADosDecimales() {
        // 12345.67 * 0.08 = 987.6536 -> 987.65
        BigDecimal impuestos = calculo.calcularImpuestos(
                new BigDecimal("12345.67"), new BigDecimal("0.08"));

        assertEquals(new BigDecimal("987.65"), impuestos);
    }
}
