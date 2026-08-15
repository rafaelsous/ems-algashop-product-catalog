package com.rafaelsousa.algashop.product.catalog.infrastructure.kafka;

import com.rafaelsousa.algashop.product.catalog.application.IntegrationEventPublisher;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic productEventTopic() {
        return TopicBuilder.name("product-catalog.product.events")
                .partitions(3)
                .replicas(3)
                .configs(Map.of("min.insync.replicas", "2"))
                .build();
    }

    @Bean
    public IntegrationEventPublisher integrationEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        return (event, destination) -> kafkaTemplate.send(destination, event);
    }
}
