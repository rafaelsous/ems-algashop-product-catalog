package com.rafaelsousa.algashop.product.catalog.domain.model.stock.event;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@ToString
public class StockReservationRejectedEvent {
    private UUID reservationId;
    private String orderId;

    @Builder.Default
    private OffsetDateTime rejectedAt = OffsetDateTime.now(Clock.systemDefaultZone());
}
