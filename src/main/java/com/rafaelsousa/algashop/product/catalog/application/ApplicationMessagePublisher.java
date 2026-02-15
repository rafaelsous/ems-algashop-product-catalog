package com.rafaelsousa.algashop.product.catalog.application;

public interface ApplicationMessagePublisher {
    void send(Object message);
}