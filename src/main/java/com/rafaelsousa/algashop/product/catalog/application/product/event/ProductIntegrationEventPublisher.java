package com.rafaelsousa.algashop.product.catalog.application.product.event;

import com.rafaelsousa.algashop.product.catalog.application.OutboundIntegrationEvent;

public interface ProductIntegrationEventPublisher {
	void send(OutboundIntegrationEvent event);
}
