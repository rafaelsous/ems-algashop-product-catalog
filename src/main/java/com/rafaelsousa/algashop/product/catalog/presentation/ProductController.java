package com.rafaelsousa.algashop.product.catalog.presentation;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @GetMapping("/{productId}")
    public ProductDetailOutput findById(@PathVariable("productId") UUID productId) {
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
                        .build())
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDetailOutput create(@RequestBody @Valid ProductInput productInput) {
        return ProductDetailOutput.builder()
                .id(UUID.randomUUID())
                .addedAt(OffsetDateTime.now())
                .name(productInput.getName())
                .brand(productInput.getBrand())
                .regularPrice(productInput.getRegularPrice())
                .salePrice(productInput.getSalePrice())
                .inStock(false)
                .enabled(productInput.getEnabled())
                .description(productInput.getDescription())
                .category(CategoryMininalOutput.builder()
                        .id(productInput.getCategoryId())
                        .name("Informática")
                        .build())
                .build();
    }
}