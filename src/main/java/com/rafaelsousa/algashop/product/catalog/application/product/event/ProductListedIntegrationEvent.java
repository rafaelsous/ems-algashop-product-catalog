package com.rafaelsousa.algashop.product.catalog.application.product.event;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductListedIntegrationEvent {
    private UUID productId;
    private OffsetDateTime listedAt;
}