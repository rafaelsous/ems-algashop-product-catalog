package com.rafaelsousa.algashop.product.catalog.infrastructure.kafka;

import com.rafaelsousa.algashop.product.catalog.application.OutboundIntegrationReply;
import com.rafaelsousa.algashop.product.catalog.application.stock.command.ReserveStockIntegrationCommand;
import com.rafaelsousa.algashop.product.catalog.application.stock.management.StockReservationApplicationService;
import com.rafaelsousa.algashop.product.catalog.application.stock.management.StockReservationInput;
import com.rafaelsousa.algashop.product.catalog.application.stock.management.StockReservationItemInput;
import com.rafaelsousa.algashop.product.catalog.application.stock.query.StockReservationOutput;
import com.rafaelsousa.algashop.product.catalog.application.stock.query.StockReservationQueryService;
import com.rafaelsousa.algashop.product.catalog.application.stock.reply.StockReservationConfirmedIntegrationReply;
import com.rafaelsousa.algashop.product.catalog.application.stock.reply.StockReservationRejectedIntegrationReply;
import com.rafaelsousa.algashop.product.catalog.infrastructure.utility.BeanValidationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
@KafkaListener(
        id = "#{algaShopMessagingKafkaProperties.stockCommandsConsumerGroup}",
        concurrency = "3",
        topics = "#{algaShopMessagingKafkaProperties.stockCommandsTopicName}")
public class KafkaStockIntegrationCommandListener {
    private final BeanValidationUtil beanValidationUtil;
    private final StockReservationQueryService stockReservationQueryService;
    private final StockReservationApplicationService stockReservationApplicationService;

    @KafkaHandler(isDefault = true)
    public void handle(
        @Payload Object command,
        @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
        @Header(value = KafkaHeaders.OFFSET, required = false) Long offset) {
        log.warn("Unsupported stock command: type={} key={} offset={}",
            command.getClass().getSimpleName(), messageKey, offset);
        throw new IllegalArgumentException(
            "Unsupported stock command type " + command.getClass().getSimpleName());
    }

    @SendTo
    @KafkaHandler
    public OutboundIntegrationReply handle(
            @Payload @Valid ReserveStockIntegrationCommand command,
            @Header(value = KafkaHeaders.CORRELATION_ID, required = false) byte[] correlationId,
            @Header(value = KafkaHeaders.REPLY_TOPIC, required = false) byte[] replyTopic,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
            @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
            @Header(value = KafkaHeaders.OFFSET, required = false) Long offset) {
        logReceived(command, messageKey, partition, offset);

        requireHeader(correlationId, KafkaHeaders.CORRELATION_ID);
        requireHeader(replyTopic, KafkaHeaders.REPLY_TOPIC);

        UUID stockReservationId = stockReservationApplicationService.reserveForOrder(this.toInput(command));
        StockReservationOutput stockReservationOutput = stockReservationQueryService.findById(stockReservationId);

        beanValidationUtil.validate(stockReservationOutput);

        return this.toReply(stockReservationOutput);
    }

    private void requireHeader(byte[] value, String name) {
        if (value == null || value.length == 0) {
            throw new IllegalArgumentException("Stock command without the " + name + " header");
        }
    }

    private OutboundIntegrationReply toReply(StockReservationOutput reservation) {
        if (reservation.isConfirmed()) {
            return StockReservationConfirmedIntegrationReply.builder()
                    .reservationId(reservation.getId())
                    .orderId(reservation.getOrderId())
                    .confirmedAt(reservation.getCreatedAt())
                    .build();
        }

        return StockReservationRejectedIntegrationReply.builder()
                .reservationId(reservation.getId())
                .orderId(reservation.getOrderId())
                .rejectedAt(reservation.getCreatedAt())
                .build();
    }

    private StockReservationInput toInput(ReserveStockIntegrationCommand command) {
        return StockReservationInput.builder()
                .orderId(command.getOrderId())
                .items(command.getItems().stream()
                        .map(item -> StockReservationItemInput.builder()
                                .productId(item.productId())
                                .quantity(item.quantity())
                                .build())
                        .toList())
                .build();
    }

    private void logReceived(Object command, String messageKey, Integer partition, Long offset) {
        log.info(
            "Received {} | key={} | partition={} | offset={} | thread={}",
            command.getClass().getSimpleName(),
            messageKey,
            partition,
            offset,
            Thread.currentThread().getName());
    }
}
