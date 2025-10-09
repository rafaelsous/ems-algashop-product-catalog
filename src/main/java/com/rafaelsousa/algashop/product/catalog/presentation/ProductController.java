package com.rafaelsousa.algashop.product.catalog.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @GetMapping("/{productId}")
    public ProductDetailOutput findById(@PathVariable("productId") UUID productId) {
        return ProductDetailOutput.builder()
                .id(productId)
                .addedAt(OffsetDateTime.now())
                .name("Notebook X11")
                .brand("Deep Diver")
                .regularPrice(BigDecimal.valueOf(1500.00))
                .salePrice(BigDecimal.valueOf(1000.00))
                .inStock(false)
                .enabled(true)
                .categoryId(UUID.randomUUID())
                .description("A Gamer Notebook")
                .build();
    }
}