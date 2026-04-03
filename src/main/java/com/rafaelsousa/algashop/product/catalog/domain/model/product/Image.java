package com.rafaelsousa.algashop.product.catalog.domain.model.product;

import com.rafaelsousa.algashop.product.catalog.domain.model.IdGenerator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.UUID;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

	@EqualsAndHashCode.Include
	private UUID id;

	private String name;

	Image(String name) {
		this(IdGenerator.generateTimeBasedUUID(), name);
	}

	public Image(UUID id, String name) {
		Objects.requireNonNull(id);

		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("Invalid image name");
		}

		this.id = id;
		this.name = name;
	}
}