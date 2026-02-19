package com.rafaelsousa.algashop.product.catalog.infrastructure.message;

import com.rafaelsousa.algashop.product.catalog.domain.model.DomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainEventPublisherConfig {

    @Bean
    public DomainEventPublisher domainEventPublisher(DomainEventPublisher domainEventPublisher) {
        return domainEventPublisher::publishEvent;
    }
}