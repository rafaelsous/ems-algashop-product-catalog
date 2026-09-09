package com.rafaelsousa.algashop.product.catalog.infrastructure.listener.product;

import com.rafaelsousa.algashop.product.catalog.application.product.event.*;
import com.rafaelsousa.algashop.product.catalog.application.utility.Mapper;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventListener {
    private final Mapper mapper;
    private final ProductIntegrationEventPublisher productIntegrationEventPublisher;

    @EventListener(ProductPriceChangedEvent.class)
    public void handle(ProductPriceChangedEvent event) {
        log.info("ProductPriceChangedEvent: {}", event);
        ProductPriceChangedIntegrationEvent integrationEvent = mapper.convert(event, ProductPriceChangedIntegrationEvent.class);
        productIntegrationEventPublisher.send(integrationEvent);

        ProductPriceChangedV2IntegrationEvent v2IntegrationEvent = mapper.convert(event, ProductPriceChangedV2IntegrationEvent.class);
        productIntegrationEventPublisher.send(v2IntegrationEvent);
    }

    @EventListener(ProductPlacedOnSaleEvent.class)
    public void handle(ProductPlacedOnSaleEvent event) {
        log.info("ProductPlacedOnSaleEvent: {}", event);
//        integrationEventPublisher.send(event, event.getProductId().toString(), "product-catalog.product.events");
    }

    @EventListener(ProductAddedEvent.class)
    public void handle(ProductAddedEvent event) {
        log.info("ProductAddedEvent: {}", event);
        ProductAddedIntegrationEvent integrationEvent = mapper.convert(event, ProductAddedIntegrationEvent.class);
        productIntegrationEventPublisher.send(integrationEvent);
    }

    @EventListener(ProductListedEvent.class)
    public void handle(ProductListedEvent event) {
        log.info("ProductListedEvent: {}", event);
        ProductListedIntegrationEvent integrationEvent = mapper.convert(event, ProductListedIntegrationEvent.class);
        productIntegrationEventPublisher.send(integrationEvent);
    }

    @EventListener(ProductDelistedEvent.class)
    public void handle(ProductDelistedEvent event) {
        log.info("ProductDelistedEvent: {}", event);
        ProductDelistedIntegrationEvent integrationEvent = mapper.convert(event, ProductDelistedIntegrationEvent.class);
        productIntegrationEventPublisher.send(integrationEvent);
    }

    @EventListener(ProductRestockedEvent.class)
    public void handle(ProductRestockedEvent event) {
        log.info("ProductRestockedEvent: {}", event);
//        integrationEventPublisher.send(event, event.getProductId().toString(), "product-catalog.product.events");
    }

    @EventListener(ProductSoldOutEvent.class)
    public void handle(ProductSoldOutEvent event) {
        log.info("ProductSoldOutEvent: {}", event);
//        integrationEventPublisher.send(event, event.getProductId().toString(), "product-catalog.product.events");
    }
}