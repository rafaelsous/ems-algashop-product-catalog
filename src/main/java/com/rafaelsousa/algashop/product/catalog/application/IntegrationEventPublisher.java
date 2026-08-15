package com.rafaelsousa.algashop.product.catalog.application;

public interface IntegrationEventPublisher {
	void send(Object event, String key, String destination);
}
