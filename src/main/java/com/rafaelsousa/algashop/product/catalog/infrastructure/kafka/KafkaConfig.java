package com.rafaelsousa.algashop.product.catalog.infrastructure.kafka;

import com.rafaelsousa.algashop.product.catalog.application.EventPublishingException;
import com.rafaelsousa.algashop.product.catalog.application.product.event.ProductIntegrationEventPublisher;
import com.rafaelsousa.algashop.product.catalog.infrastructure.utility.BeanValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
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
        KafkaTemplate<String, Object> kafkaTemplate, BeanValidationUtil beanValidationUtil) {
        return event -> {
            beanValidationUtil.validate(event);

            SendResult<String, Object> result;
            try {
	            ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(algaShopMessagingKafkaProperties.getProductEventTopicName(), event.getAggregateId(), event);

	            producerRecord.headers().add("idempotency-key", event.getIdempotencyKey().toString().getBytes());

				result = kafkaTemplate.send(producerRecord).get(40, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new EventPublishingException("Interrupted while publishing", event, ex);
            } catch (TimeoutException | ExecutionException ex) {
                throw new EventPublishingException("Failed to publish event", event, ex);
            }

	        RecordMetadata metadata = result.getRecordMetadata();

	        log.info(
		        "Published {} to {}-{} at offset {}",
		        event.getClass().getSimpleName(),
		        metadata.topic(),
		        metadata.partition(),
		        metadata.offset());
        };
    }
}
