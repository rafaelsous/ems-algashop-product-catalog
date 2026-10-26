package com.rafaelsousa.algashop.product.catalog.domain.model.stock.event;

import com.rafaelsousa.algashop.product.catalog.domain.model.stock.StockMovement;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@ToString
public class StockMovementRegisteredEvent {
    private UUID movementId;
    private UUID productId;
    private StockMovement.MovementType type;
    private String orderId;
    private Integer movementQuantity;
    private Integer previousQuantity;
    private Integer newQuantity;

    @Builder.Default
    private OffsetDateTime occurredAt = OffsetDateTime.now(Clock.systemDefaultZone());

    public boolean causedSoldOut() {
        return newQuantity != null && previousQuantity != null
                && newQuantity == 0 && previousQuantity != 0;
    }

    public boolean causedRestock() {
        return newQuantity != null && previousQuantity != null
                && newQuantity > 0 && previousQuantity == 0;
    }
}
