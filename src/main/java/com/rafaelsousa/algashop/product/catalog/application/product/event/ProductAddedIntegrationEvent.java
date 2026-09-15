package com.rafaelsousa.algashop.product.catalog.application.product.event;

import com.rafaelsousa.algashop.product.catalog.application.IntegrationEvent;
import com.rafaelsousa.algashop.product.catalog.domain.model.IdGenerator;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductAddedIntegrationEvent implements IntegrationEvent {
    private UUID productId;
    private UUID idempotencyKey = IdGenerator.generateTimeBasedUUID();
    private OffsetDateTime addedAt;

    @Override
    public String getAggregateId() {
        if (productId == null) {
            return null;
        }

        return productId.toString();
    }
}
