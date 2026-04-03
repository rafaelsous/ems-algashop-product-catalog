package com.rafaelsousa.algashop.product.catalog.infrastructure.utility.mapper;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Component
@Getter @Setter
@ConfigurationProperties("algashop.mapping")
public class ApplicationMappingProperty {

	@NotBlank
	private String imageStorageUrl;
}