package com.rafaelsousa.algashop.product.catalog.infrastructure.kafka;

import com.rafaelsousa.algashop.product.catalog.application.product.event.ProductIntegrationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    private final AlgaShopMessagingKafkaProperties algaShopMessagingKafkaProperties;

    @Bean
    public NewTopic productEventTopic() {
        return TopicBuilder.name(algaShopMessagingKafkaProperties.getProductEventTopicName())
                .partitions(3)
                .replicas(3)
                .configs(Map.of("min.insync.replicas", "2"))
                .build();
    }

    @Bean
    public ProductIntegrationEventPublisher productIntegrationEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate) {
        return event ->
                kafkaTemplate.send(
                        algaShopMessagingKafkaProperties.getProductEventTopicName(),
                        event.getAggregateId(),
                        event);
    }
}
