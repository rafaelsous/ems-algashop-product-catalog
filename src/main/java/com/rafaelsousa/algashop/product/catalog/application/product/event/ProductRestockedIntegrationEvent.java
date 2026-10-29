package com.rafaelsousa.algashop.product.catalog.application.product.event;

import com.rafaelsousa.algashop.product.catalog.application.OutboundIntegrationEvent;
import com.rafaelsousa.algashop.product.catalog.domain.model.IdGenerator;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRestockedIntegrationEvent implements OutboundIntegrationEvent {
    private UUID productId;
    private OffsetDateTime restockedAt;
    private UUID idempotencyKey = IdGenerator.generateTimeBasedUUID();

    @Override
    public String getAggregateId() {
        if (productId == null) {
            return null;
        }

        return productId.toString();
    }

    @Override
    public UUID getIdempotencyKey() {
        return idempotencyKey;
    }
}
