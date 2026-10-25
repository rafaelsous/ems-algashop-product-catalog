package com.rafaelsousa.algashop.product.catalog.application.category.query;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CategoryDetailOutputTestDataBuilder {
    private CategoryDetailOutputTestDataBuilder() {}

    public static CategoryDetailOutput.CategoryDetailOutputBuilder aCategory() {
        UUID categoryId = UUID.randomUUID();

        return CategoryDetailOutput.builder()
                .id(categoryId)
                .name("Informática")
                .updatedAt(OffsetDateTime.now())
                .enabled(true);
    }

    public static CategoryDetailOutput.CategoryDetailOutputBuilder aCategoryAlt() {
        return CategoryDetailOutput.builder()
                .id(UUID.randomUUID())
                .name("Escritório")
                .updatedAt(OffsetDateTime.now())
                .enabled(false);
    }
}
