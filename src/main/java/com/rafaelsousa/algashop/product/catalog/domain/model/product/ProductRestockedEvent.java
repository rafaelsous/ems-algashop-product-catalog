package com.rafaelsousa.algashop.product.catalog.domain.model.product;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ProductRestockedEvent {
	private UUID productId;

	@Builder.Default
	private OffsetDateTime restockedAt = OffsetDateTime.now();
}