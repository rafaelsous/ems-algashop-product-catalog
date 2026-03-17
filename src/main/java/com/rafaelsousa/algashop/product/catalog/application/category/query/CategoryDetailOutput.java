package com.rafaelsousa.algashop.product.catalog.application.category.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDetailOutput implements Serializable {
    private UUID id;
    private String name;
    private Boolean enabled;

	private Long version;
	private OffsetDateTime updatedAt;
}