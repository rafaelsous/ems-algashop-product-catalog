package com.rafaelsousa.algashop.product.catalog.domain.model;

public interface DomainEventPublisher {
    void publishEvent(Object event);
}