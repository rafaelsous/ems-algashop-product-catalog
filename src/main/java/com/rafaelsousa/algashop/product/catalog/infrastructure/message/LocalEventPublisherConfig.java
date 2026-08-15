package com.rafaelsousa.algashop.product.catalog.infrastructure.message;

import com.rafaelsousa.algashop.product.catalog.application.LocalEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocalEventPublisherConfig {

    @Bean
    public LocalEventPublisher localEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return applicationEventPublisher::publishEvent;
    }
}