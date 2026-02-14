package com.rafaelsousa.algashop.product.catalog.application.product.query;

import com.rafaelsousa.algashop.product.catalog.application.category.query.CategoryMininalOutput;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.rafaelsousa.algashop.product.catalog.infrastructure.utility.Slugfier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummaryOutput {
    private UUID id;
    private OffsetDateTime createdAt;
    private String name;
    private String brand;
    private BigDecimal regularPrice;
    private BigDecimal salePrice;
    private Boolean inStock;
    private Boolean enabled;
    private String shortDescription;
    private CategoryMininalOutput category;

    private Boolean hasDiscount;
    private Integer quantityInStock;
    private Integer discountPercentageRounded;

    private Float score;

    public String getSlug() {
        return Slugfier.slugify(this.getName());
    }
}