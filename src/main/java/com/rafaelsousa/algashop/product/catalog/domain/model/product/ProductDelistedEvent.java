package com.rafaelsousa.algashop.product.catalog.domain.model.product;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ProductDelistedEvent {
    private UUID productId;

    @Builder.Default
    private OffsetDateTime delistedAt = OffsetDateTime.now();
}