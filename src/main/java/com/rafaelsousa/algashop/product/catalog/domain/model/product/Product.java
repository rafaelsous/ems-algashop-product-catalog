package com.rafaelsousa.algashop.product.catalog.domain.model.product;

import com.rafaelsousa.algashop.product.catalog.domain.model.DomainException;
import com.rafaelsousa.algashop.product.catalog.domain.model.IdGenerator;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "products")
public class Product {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    private String name;
    private String brand;

    @Setter
    private String description;
    private Integer quantityInStock;
    private Boolean enabled;
    private BigDecimal regularPrice;
    private BigDecimal salePrice;

    @Version
    private Long version;

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime updatedAt;

    @CreatedBy
    private UUID createdByUserId;

    @LastModifiedBy
    private UUID lastModifiedByUserId;

    @Builder
    public Product(String name, String brand, String description, Boolean enabled,
                   BigDecimal regularPrice, BigDecimal salePrice) {
        this.setId(IdGenerator.generateTimeBasedUUID());
        
        this.setName(name);
        this.setBrand(brand);
        this.setDescription(description);
        this.setEnabled(enabled);
        this.setRegularPrice(regularPrice);
        this.setSalePrice(salePrice);
        this.setQuantityInStock(0);
    }

    public void setName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException();
        }

        this.name = name;
    }

    public void setBrand(String brand) {
        if (StringUtils.isBlank(brand)) {
            throw new IllegalArgumentException();
        }

        this.brand = brand;
    }

    public void setRegularPrice(BigDecimal regularPrice) {
        Objects.requireNonNull(regularPrice);

        if (regularPrice.signum() == -1) {
            throw new IllegalArgumentException();
        }

        if (Objects.isNull(this.salePrice)) {
            this.salePrice = regularPrice;
        } else if (this.salePrice.compareTo(regularPrice) > 0) {
            throw new DomainException("Sale price cannot be greater than regular price");
        }

        this.regularPrice = regularPrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        Objects.requireNonNull(salePrice);

        if (salePrice.signum() == -1) {
            throw new IllegalArgumentException();
        }

        if (Objects.isNull(this.regularPrice)) {
            this.regularPrice = salePrice;
        } else if (salePrice.compareTo(this.regularPrice) > 0) {
            throw new DomainException("Sale price cannot be greater than regular price");
        }

        this.salePrice = salePrice;
    }

    public void setEnabled(Boolean enabled) {
        Objects.requireNonNull(enabled);

        this.enabled = enabled;
    }

    public void disable() {
        this.setEnabled(false);
    }

    public void enable() {
        this.setEnabled(true);
    }

    public boolean isInStock() {
        return Objects.nonNull(quantityInStock) && quantityInStock > 0;
    }

    private void setId(UUID id) {
        Objects.requireNonNull(id);

        this.id = id;
    }

    private void setQuantityInStock(Integer quantityInStock) {
        Objects.requireNonNull(quantityInStock);

        if (quantityInStock < 0) {
            throw new IllegalArgumentException();
        }

        this.quantityInStock = quantityInStock;
    }
}