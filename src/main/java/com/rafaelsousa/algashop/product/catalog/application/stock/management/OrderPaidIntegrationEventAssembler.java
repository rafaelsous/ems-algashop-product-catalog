package com.rafaelsousa.algashop.product.catalog.application.stock.management;

import com.rafaelsousa.algashop.product.catalog.application.order.event.OrderPaidIntegrationEvent;
import com.rafaelsousa.algashop.product.catalog.application.order.snapshot.OrderItemSnapshot;

import org.springframework.stereotype.Component;

@Component
public class OrderPaidIntegrationEventAssembler {

    public StockReservationInput toStockReservationInput(
            OrderPaidIntegrationEvent orderPaidIntegrationEvent) {
        return StockReservationInput.builder()
                .orderId(orderPaidIntegrationEvent.getOrderId())
                .items(
                        orderPaidIntegrationEvent.getItems().stream()
                                .map(this::toStockReservationItemInput)
                                .toList())
                .build();
    }

    private StockReservationItemInput toStockReservationItemInput(OrderItemSnapshot item) {
        return StockReservationItemInput.builder()
                .productId(item.productId())
                .quantity(item.quantity())
                .build();
    }
}
