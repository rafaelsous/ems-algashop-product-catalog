package com.rafaelsousa.algashop.product.catalog.domain.model.product;

import com.rafaelsousa.algashop.product.catalog.domain.model.DomainEventPublisher;
import com.rafaelsousa.algashop.product.catalog.domain.model.DomainException;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.QuantityInStockAdjustment.Result;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {
	private final DomainEventPublisher domainEventPublisher;
	private final QuantityInStockAdjustment quantityInStockAdjustment;

	public void restock(Product product, int quantity) {
		Objects.requireNonNull(product);

		if (quantity < 1) {
			throw new IllegalArgumentException();
		}

		Result result;
		try {
			result = quantityInStockAdjustment.increase(product.getId(), quantity);
		} catch (Exception _) {
            throw new DomainException(String.format("Failed to restock product %s on stock", product.getId()));
		}

		if (result.inRestocked()) {
			domainEventPublisher.publishEvent(ProductRestockedEvent.builder()
					.productId(product.getId())
					.build());
		}
	}

	public void withdraw(Product product, int quantity) {
		Objects.requireNonNull(product);

		if (quantity < 1) {
			throw new IllegalArgumentException();
		}

		Result result;
		try {
			result = quantityInStockAdjustment.decrease(product.getId(), quantity);
		} catch (Exception _) {
			throw new DomainException(String.format("Failed to withdrawn product %s from stock", product.getId()));
		}

		if (result.isOutOfStock()) {
			domainEventPublisher.publishEvent(ProductSoldOutEvent.builder()
					.productId(product.getId())
					.build());
		}
	}
}