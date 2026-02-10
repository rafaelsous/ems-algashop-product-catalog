package com.rafaelsousa.algashop.product.catalog.application.product.query;

import com.rafaelsousa.algashop.product.catalog.application.utility.SortablePageFilter;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;
import org.springframework.data.domain.Sort;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductFilter extends SortablePageFilter<ProductFilter.SortType> {
    private String term;
    private Boolean hasDiscount;
    private Boolean enabled;
    private Boolean inStock;
    private BigDecimal priceFrom;
    private BigDecimal priceTo;
    private UUID[] categoriesId;
    private OffsetDateTime createdAtFrom;
    private OffsetDateTime createdAtTo;

    @Override
    public SortType getSortByPropertyOrDefault() {
        return getSortByProperty() == null ? SortType.CREATED_AT : getSortByProperty();
    }

    @Override
    public Sort.Direction getSortDirectionOrDefault() {
        return getSortDirection() == null ? Sort.Direction.ASC : getSortDirection();
    }

    @Getter
    @RequiredArgsConstructor
    public enum SortType {
        CREATED_AT("createdAt"),
        SALE_PRICE("salePrice");

        private final String propertyName;
    }
}