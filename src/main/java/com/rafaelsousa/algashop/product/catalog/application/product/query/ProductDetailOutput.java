package com.rafaelsousa.algashop.product.catalog.application.product.query;

import com.rafaelsousa.algashop.product.catalog.application.category.query.CategoryMininalOutput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailOutput implements Serializable {
    private UUID id;
    private OffsetDateTime createdAt;
    private String name;
    private String brand;
    private BigDecimal regularPrice;
    private BigDecimal salePrice;
    private Boolean inStock;
    private Boolean enabled;
    private String description;
    private CategoryMininalOutput category;

    private String slug;
    private Boolean hasDiscount;
    private Integer quantityInStock;
    private Integer discountPercentageRounded;

	private Long version;
	private OffsetDateTime updatedAt;

	private ImageOutput mainImage;
}