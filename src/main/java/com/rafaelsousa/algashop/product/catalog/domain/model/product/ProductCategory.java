package com.rafaelsousa.algashop.product.catalog.domain.model.product;

import com.rafaelsousa.algashop.product.catalog.domain.model.category.Category;
import java.util.UUID;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductCategory {
    private UUID id;
    private String name;
    private Boolean enabled;
    
    public static ProductCategory of(Category category) {
        return ProductCategory.builder()
                .id(category.getId())
                .name(category.getName())
                .enabled(category.getEnabled())
            .build(); 
    }
}