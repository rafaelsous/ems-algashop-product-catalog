package com.rafaelsousa.algashop.product.catalog.application.product.query;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class ProductDetailOutputTestDataBuilder {
    private ProductDetailOutputTestDataBuilder() {
    }

    public static ProductDetailOutput.ProductDetailOutputBuilder aProduct() {
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        return ProductDetailOutput.builder()
                .id(productId)
                .addedAt(OffsetDateTime.now())
                .name("Notebook X11")
                .brand("Deep Diver")
                .regularPrice(BigDecimal.valueOf(1500.00))
                .salePrice(BigDecimal.valueOf(1000.00))
                .inStock(true)
                .enabled(true)
                .description("A Gamer Notebook")
                .category(CategoryMininalOutput.builder()
                        .id(categoryId)
                        .name("Informática")
                        .build());
    }

    public static ProductDetailOutput.ProductDetailOutputBuilder aProductAlt() {
        return ProductDetailOutput.builder()
                .id(UUID.randomUUID())
                .addedAt(OffsetDateTime.now())
                .name("Interruptor")
                .brand("Tramontina")
                .regularPrice(BigDecimal.valueOf(150.00))
                .salePrice(BigDecimal.valueOf(100.00))
                .inStock(true)
                .enabled(true)
                .description("Interruptor Inteligente")
                .category(CategoryMininalOutput.builder()
                        .id(UUID.randomUUID())
                        .name("Casa Inteligente")
                        .build());
    }
}