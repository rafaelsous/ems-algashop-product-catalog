package com.rafaelsousa.algashop.product.catalog.application.stock.event;

import com.rafaelsousa.algashop.product.catalog.application.IntegrationEvent;

public interface StockIntegrationEventPublisher {
	void send(IntegrationEvent event);
}
