package com.rafaelsousa.algashop.product.catalog.domain.model.category.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CategoryUpdateEvent {
    private UUID categoryId;
    private String name;
    private Boolean enabled;
}