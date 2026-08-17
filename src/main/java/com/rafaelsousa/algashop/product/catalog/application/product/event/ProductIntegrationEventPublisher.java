package com.rafaelsousa.algashop.product.catalog.application.product.event;

import com.rafaelsousa.algashop.product.catalog.application.IntegrationEvent;

public interface ProductIntegrationEventPublisher {
	void send(IntegrationEvent event);
}
