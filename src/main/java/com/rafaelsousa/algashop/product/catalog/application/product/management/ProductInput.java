package com.rafaelsousa.algashop.product.catalog.application.product.management;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInput {

    @NotBlank
    private String name;

    @NotBlank
    private String brand;

    @NotNull
    private BigDecimal regularPrice;

    @NotNull
    private BigDecimal salePrice;

    private String description;

    @NotNull
    private Boolean enabled;

    @NotNull
    private UUID categoryId;
}