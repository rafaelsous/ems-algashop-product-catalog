package com.rafaelsousa.algashop.product.catalog.infrastructure.listener.order;

import com.rafaelsousa.algashop.product.catalog.application.order.event.OrderPaidIntegrationEvent;
import com.rafaelsousa.algashop.product.catalog.application.stock.management.OrderPaidIntegrationEventAssembler;
import com.rafaelsousa.algashop.product.catalog.application.stock.management.StockReservationApplicationService;
import com.rafaelsousa.algashop.product.catalog.application.stock.management.StockReservationInput;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(
        id = "#{algaShopMessagingKafkaProperties.orderEventsConsumerGroup}",
        concurrency = "3",
        topics = "#{algaShopMessagingKafkaProperties.orderEventTopicName}")
public class KafkaOrderIntegrationEventListener {
    private final OrderPaidIntegrationEventAssembler orderPaidIntegrationEventAssembler;
    private final StockReservationApplicationService stockReservationApplicationService;

    @KafkaHandler(isDefault = true)
    public void handle(
            @Payload Object event,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
            @Header(value = KafkaHeaders.OFFSET, required = false) Long offset) {
        log.info(
                "Event ignored: type={} key={} offset={}",
                event.getClass().getSimpleName(),
                messageKey,
                offset);
    }

    @KafkaHandler
    public void handle(
            @Payload @Valid OrderPaidIntegrationEvent event,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
            @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
            @Header(value = KafkaHeaders.OFFSET, required = false) Long offset) {
        logReceived(event, messageKey, partition, offset);

        StockReservationInput stockReservationInput = orderPaidIntegrationEventAssembler.toStockReservationInput(event);
        stockReservationApplicationService.reserveForOrder(stockReservationInput);
    }

    private void logReceived(Object event, String messageKey, Integer partition, Long offset) {
        log.info(
                "Received {} | key={} | partiton={} | offset={} | thread={}",
                event.getClass().getSimpleName(),
                messageKey,
                partition,
                offset,
                Thread.currentThread().getName());
    }
}
