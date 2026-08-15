package com.rafaelsousa.algashop.product.catalog.application;

public interface LocalEventPublisher {
    void send(Object message);
}