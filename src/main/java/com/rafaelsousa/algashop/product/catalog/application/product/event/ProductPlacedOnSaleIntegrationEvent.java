package com.rafaelsousa.algashop.product.catalog.application.product.event;

import com.rafaelsousa.algashop.product.catalog.application.OutboundIntegrationEvent;
import com.rafaelsousa.algashop.product.catalog.domain.model.IdGenerator;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPlacedOnSaleIntegrationEvent implements OutboundIntegrationEvent {
    private UUID productId;
    private BigDecimal salePrice;
    private BigDecimal regularPrice;
    private OffsetDateTime placedOnSaleAt;
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
