package com.rafaelsousa.algashop.product.catalog.infrastructure.kafka;

import com.rafaelsousa.algashop.product.catalog.application.EventPublishingException;
import com.rafaelsousa.algashop.product.catalog.application.OutboundIntegrationEvent;
import com.rafaelsousa.algashop.product.catalog.application.product.event.ProductIntegrationEventPublisher;
import com.rafaelsousa.algashop.product.catalog.domain.model.DomainException;
import com.rafaelsousa.algashop.product.catalog.infrastructure.persistence.product.StockUpdateFailedException;
import com.rafaelsousa.algashop.product.catalog.infrastructure.utility.BeanValidationUtil;

import jakarta.validation.ConstraintViolationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.backoff.ExponentialBackOff;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    public static final String TYPE_ID_HEADER = "__TypeId__";
    public static final String MIN_INSYNC_REPLICAS_CONFIG = "min.insync.replicas";
    public static final String IDEMPOTENCY_KEY_HEADER = "idempotency-key";

    private static final String DLT_PREFIX = "product-catalog.dlt.";
    private static final int TOPIC_REPLICAS = 3;
    private static final int TOPIC_PARTITIONS = 3;
    private static final String RETENTION_30_DAYS = String.valueOf(Duration.ofDays(30).toMillis());

    private final AlgaShopMessagingKafkaProperties properties;

    @Bean
    public DefaultErrorHandler defaultErrorHandler(DeadLetterPublishingRecoverer recoverer) {
        long interval = 2000L; // 2s entre as tentativas
        double multiplier = 2;
        long maxRetries = 3L; // máximo de 3 tentativas
        long maxInterval = 8000L;

        ExponentialBackOff exponentialBackOff = new ExponentialBackOff(interval, multiplier);
        exponentialBackOff.setMaxInterval(maxInterval);
        exponentialBackOff.setMaxAttempts(maxRetries);

        DefaultErrorHandler defaultErrorHandler =
                new DefaultErrorHandler(recoverer, exponentialBackOff);
        defaultErrorHandler.addRetryableExceptions(StockUpdateFailedException.class);
        defaultErrorHandler.addNotRetryableExceptions(
                DomainException.class,
                ConstraintViolationException.class,
                DataIntegrityViolationException.class,
                DuplicateKeyException.class,
                IllegalArgumentException.class);

        return defaultErrorHandler;
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(
            KafkaTemplate<String, Object> kafkaTemplate,
            AlgaShopMessagingKafkaProperties properties) {
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (consumerRecord, _) -> {
                            String dltTopic = DLT_PREFIX + consumerRecord.topic();
                            return new TopicPartition(dltTopic, consumerRecord.partition());
                        });

        recoverer.setLogRecoveryRecord(true);
        recoverer.setFailIfSendResultIsError(false);

        return recoverer;
    }

    @Bean
    public NewTopic productEventTopic() {
        return TopicBuilder.name(properties.getProductEventTopicName())
                .partitions(TOPIC_PARTITIONS)
                .replicas(TOPIC_REPLICAS)
                .configs(Map.of(MIN_INSYNC_REPLICAS_CONFIG, "2"))
                .build();
    }

    @Bean
    public ProductIntegrationEventPublisher productIntegrationEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            BeanValidationUtil beanValidationUtil,
            AlgaShopMessagingKafkaProperties properties) {
        return event ->
                publish(
                        kafkaTemplate,
                        beanValidationUtil,
                        event,
                        properties.getProductEventTopicName());
    }

    private void publish(
            KafkaTemplate<String, Object> kafkaTemplate,
            BeanValidationUtil beanValidationUtil,
            OutboundIntegrationEvent event,
            String targetTopicName) {
        beanValidationUtil.validate(event);

        SendResult<String, Object> result;
        try {
            ProducerRecord<String, Object> producerRecord =
                    new ProducerRecord<>(targetTopicName, event.getAggregateId(), event);

            if (event.getIdempotencyKey() != null) {
                producerRecord
                        .headers()
                        .add(
                                IDEMPOTENCY_KEY_HEADER,
                                event.getIdempotencyKey().toString().getBytes());
            }

            result = kafkaTemplate.send(producerRecord).get(40, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new EventPublishingException("Interrupted while publishing", event, ex);
        } catch (TimeoutException | ExecutionException | KafkaException ex) {
            throw new EventPublishingException("Failed to publish event", event, ex);
        }

        RecordMetadata metadata = result.getRecordMetadata();

        log.info(
                "Published {} to {}-{} at offset {}",
                event.getClass().getSimpleName(),
                metadata.topic(),
                metadata.partition(),
                metadata.offset());
    }

    private NewTopic createDeadLetterTopic(String originTopicName) {
        return TopicBuilder.name(DLT_PREFIX + originTopicName)
                .partitions(TOPIC_PARTITIONS)
                .replicas(TOPIC_REPLICAS)
                .configs(Map.of(MIN_INSYNC_REPLICAS_CONFIG, "2", "retention.ms", RETENTION_30_DAYS))
                .build();
    }
}
