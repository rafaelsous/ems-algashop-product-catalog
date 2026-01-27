package com.rafaelsousa.algashop.product.catalog.domain.model.product;

import com.rafaelsousa.algashop.product.catalog.domain.model.IdGenerator;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;
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
    private String description;
    private Integer quantityInStock;
    private Boolean enable;
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
    public Product(String name, String brand, String description, Boolean enable,
                   BigDecimal regularPrice, BigDecimal salePrice) {
        this.id = IdGenerator.generateTimeBasedUUID();
        
        this.name = name;
        this.brand = brand;
        this.description = description;
        this.enable = enable;
        this.regularPrice = regularPrice;
        this.salePrice = salePrice;
    }
}