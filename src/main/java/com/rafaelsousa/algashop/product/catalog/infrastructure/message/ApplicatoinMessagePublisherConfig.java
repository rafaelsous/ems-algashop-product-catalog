package com.rafaelsousa.algashop.product.catalog.infrastructure.message;

import com.rafaelsousa.algashop.product.catalog.application.ApplicationMessagePublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicatoinMessagePublisherConfig {

    @Bean
    public ApplicationMessagePublisher applicationMessagePublisher(ApplicationEventPublisher applicationEventPublisher) {
        return applicationEventPublisher::publishEvent;
    }
}