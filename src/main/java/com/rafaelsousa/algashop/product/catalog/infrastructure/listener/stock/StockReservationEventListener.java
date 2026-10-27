package com.rafaelsousa.algashop.product.catalog.infrastructure.listener.stock;

import com.rafaelsousa.algashop.product.catalog.application.stock.event.StockIntegrationEventPublisher;
import com.rafaelsousa.algashop.product.catalog.application.stock.event.StockReservationConfirmedIntegrationEvent;
import com.rafaelsousa.algashop.product.catalog.application.stock.event.StockReservationRejectedIntegrationEvent;
import com.rafaelsousa.algashop.product.catalog.application.utility.Mapper;
import com.rafaelsousa.algashop.product.catalog.domain.model.stock.event.StockReservationConfirmedEvent;
import com.rafaelsousa.algashop.product.catalog.domain.model.stock.event.StockReservationRejectedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockReservationEventListener {
	private final Mapper mapper;
    private final StockIntegrationEventPublisher stockIntegrationEventPublisher;

    @Async
    @TransactionalEventListener
    public void handle(StockReservationConfirmedEvent event) {
        log.info("StockReservationConfirmedEvent: {}", event);
        StockReservationConfirmedIntegrationEvent integrationEvent =
                mapper.convert(event, StockReservationConfirmedIntegrationEvent.class);
        stockIntegrationEventPublisher.send(integrationEvent);
    }

    @Async
    @TransactionalEventListener
    public void handle(StockReservationRejectedEvent event) {
        log.info("StockReservationRejectedEvent: {}", event);
        StockReservationRejectedIntegrationEvent integrationEvent =
                mapper.convert(event, StockReservationRejectedIntegrationEvent.class);
        stockIntegrationEventPublisher.send(integrationEvent);
    }
}
