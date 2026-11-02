package com.rafaelsousa.algashop.product.catalog.infrastructure.kafka;

import com.rafaelsousa.algashop.product.catalog.application.InboundIntegrationCommand;
import com.rafaelsousa.algashop.product.catalog.application.stock.command.ReserveStockIntegrationCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaBeanValidationConfigurerTest {
	private LocalValidatorFactoryBean validator;
	private KafkaListenerEndpointRegistrar registrar;

	@BeforeEach
	void setup() {
		validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();

		registrar = new KafkaListenerEndpointRegistrar();
		KafkaBeanValidationConfigurer kafkaBeanValidationConfigurer = new KafkaBeanValidationConfigurer(validator);
		kafkaBeanValidationConfigurer.configureKafkaListeners(registrar);
	}

	@Test
	void shouldRegisterValidator() {
		assertThat(registrar.getValidator()).isSameAs(validator);
	}

	@Test
	void shouldShowViolationWhenGivenInvalidPayload() {
		ReserveStockIntegrationCommand invalidCommand = ReserveStockIntegrationCommand.builder()
			.orderId("0S3PK28SZDZ8K")
			.build();
		Errors errors = new BeanPropertyBindingResult(invalidCommand, "reserveStockIntegrationCommand");

		validateCommandPayload(invalidCommand, errors);

		assertThat(errors).satisfies(errs -> {
			assertThat(errs.hasErrors()).isTrue();
			assertThat(errs.getFieldError("items")).isNotNull();
		});
	}

	@Test
	void shouldNotShowViolationWhenGivenValidPayload() {
		ReserveStockIntegrationCommand validCommand = ReserveStockIntegrationCommand.builder()
			.orderId("0S3PK28SZDZ8K")
			.items(List.of(
				new ReserveStockIntegrationCommand.Item(UUID.randomUUID(), 10),
				new ReserveStockIntegrationCommand.Item(UUID.randomUUID(), 5)
			))
			.build();
		Errors errors = new BeanPropertyBindingResult(validCommand, "reserveStockIntegrationCommand");

		validateCommandPayload(validCommand, errors);

		assertThat(errors.hasErrors()).isFalse();
	}

	private void validateCommandPayload(InboundIntegrationCommand command, Errors errors) {
		assert registrar.getValidator() != null;
		registrar.getValidator().validate(command, errors);
	}
}
