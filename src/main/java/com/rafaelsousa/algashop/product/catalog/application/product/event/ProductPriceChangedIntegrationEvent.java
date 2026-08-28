package com.rafaelsousa.algashop.product.catalog.application.product.event;

import com.rafaelsousa.algashop.product.catalog.application.IntegrationEvent;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceChangedIntegrationEvent implements IntegrationEvent {
	private UUID productId;
	private OffsetDateTime changedAt;

	@Override
	public String getAggregateId() {
		if (productId == null) {
			return null;
		}

		return productId.toString();
	}
}
