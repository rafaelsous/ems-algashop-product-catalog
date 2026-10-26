package com.rafaelsousa.algashop.product.catalog.infrastructure.listener.stock;

import com.rafaelsousa.algashop.product.catalog.domain.model.DomainEventPublisher;

import com.rafaelsousa.algashop.product.catalog.domain.model.product.ProductRestockedEvent;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.ProductSoldOutEvent;
import com.rafaelsousa.algashop.product.catalog.domain.model.stock.event.StockMovementRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockMovementEventListener {
    private final DomainEventPublisher domainEventPublisher;

	@EventListener
    public void handle(StockMovementRegisteredEvent event) {
        log.info("Stock movement registered: {}", event);

        if (event.causedSoldOut()) {
            domainEventPublisher.publishEvent(
                    ProductSoldOutEvent.builder()
                            .productId(event.getProductId())
                            .build());
        }

		if (event.causedRestock()) {
			domainEventPublisher.publishEvent(
				ProductRestockedEvent.builder()
							.productId(event.getProductId())
							.build());
		}
    }

	@CacheEvict(cacheNames = "algashop:products:v1", key = "#event.productId")
	@TransactionalEventListener
	public void handleEvictProductCache(StockMovementRegisteredEvent event) {
		log.info("StockMovementRegisteredEvent handle product {} cache eviction", event.getProductId());
	}
}
