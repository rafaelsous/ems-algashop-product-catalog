package com.rafaelsousa.algashop.product.catalog.application.storage;

import lombok.Builder;
import org.springframework.http.MediaType;

import java.time.Duration;
import java.util.Objects;

public record FileReference(String fileName, MediaType contentType, Long contentLength, Duration expiresIn) {

	@Builder
	public FileReference {
		Objects.requireNonNull(fileName);
		Objects.requireNonNull(contentType);
		Objects.requireNonNull(expiresIn);

		if (contentLength <= 0) {
			throw new IllegalArgumentException("Content length must be greater than zero.");
		}
	}
}