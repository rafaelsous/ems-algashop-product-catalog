package com.rafaelsousa.algashop.product.catalog.infrastructure.persistence.product;

import com.rafaelsousa.algashop.product.catalog.application.product.query.*;
import com.rafaelsousa.algashop.product.catalog.application.utility.Mapper;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.Product;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.ProductNotFoundException;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.ProductRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.AggregationExpressionCriteria;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductQueryServiceImpl implements ProductQueryService {
    public static final String CREATED_AT_PROPERTY_NAME = "createdAt";
    public static final String SALE_PRICE_PROPERTY_NAME = "salePrice";

    private final ProductRepository productRepository;
    private final Mapper mapper;
    private final MongoOperations mongoOperations;

    @Override
    public ProductDetailOutput findById(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        return mapper.convert(product, ProductDetailOutput.class);
    }

    @Override
    public PageModel<ProductSummaryOutput> filter(ProductFilter filter) {
        Query query = queryWith(filter);
        long totalItems = mongoOperations.count(query, Product.class);

        Sort sort = sortWith(filter);

        PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        Query pagedQuery = query.with(pageRequest);

        int totalPages = 0;
        List<Product> products;

        if (totalItems > 0) {
            products = mongoOperations.find(pagedQuery, Product.class);
            totalPages = (int) Math.ceil((double) totalItems / pageRequest.getPageSize());
        } else {
            products = new ArrayList<>();
        }

        List<ProductSummaryOutput> productSummaryOutputs = products.stream()
                .map(product -> mapper.convert(product, ProductSummaryOutput.class))
                .toList();

    return PageModel.<ProductSummaryOutput>builder()
        .content(productSummaryOutputs)
            .size(pageRequest.getPageSize())
            .number(pageRequest.getPageNumber())
            .totalElements(totalItems)
            .totalPages(totalPages)
        .build();
    }

    private Query queryWith(ProductFilter filter) {
        Query query = new Query();

        filterByEnabled(filter.getEnabled(), query);
        filterByCreationDateRange(filter.getCreatedAtFrom(), filter.getCreatedAtTo(), query);
        filterByPriceRange(filter.getPriceFrom(), filter.getPriceTo(), query);
        filterByHasDiscount(filter.getHasDiscount(), query);
        filterByInStock(filter.getInStock(), query);
        filterByCategoriesId(filter.getCategoriesId(), query);
        filterByTerm(filter.getTerm(), query);

        return query;
    }

    private Sort sortWith(ProductFilter filter) {
        if (StringUtils.isNotBlank(filter.getTerm())) {
            return Sort.by("score");
        }

        return Sort.by(filter.getSortDirectionOrDefault(), filter.getSortByPropertyOrDefault().getPropertyName());
    }

    private static void filterByEnabled(Boolean isEnabled, Query query) {
        if (isEnabled != null) {
            query.addCriteria(Criteria.where("enabled").is(isEnabled));
        }
    }

    private static void filterByCreationDateRange(OffsetDateTime createdAtFrom, OffsetDateTime createdAtTo, Query query) {
        if (createdAtFrom != null && createdAtTo != null) {
            query.addCriteria(Criteria.where(CREATED_AT_PROPERTY_NAME)
                    .gte(createdAtFrom)
                    .lte(createdAtTo)
            );
        } else {
            if (createdAtFrom != null) {
                query.addCriteria(Criteria.where(CREATED_AT_PROPERTY_NAME).gte(createdAtFrom));
            } else if (createdAtTo != null) {
                query.addCriteria(Criteria.where(CREATED_AT_PROPERTY_NAME).lte(createdAtTo));
            }
        }
    }

    private static void filterByPriceRange(BigDecimal priceFrom, BigDecimal priceTo, Query query) {
        if (priceFrom != null && priceTo != null) {
            query.addCriteria(Criteria.where(SALE_PRICE_PROPERTY_NAME)
                    .gte(priceFrom)
                    .lte(priceTo)
            );
        } else {
            if (priceFrom != null) {
                query.addCriteria(Criteria.where(SALE_PRICE_PROPERTY_NAME).gte(priceFrom));
            } else if (priceTo != null) {
                query.addCriteria(Criteria.where(SALE_PRICE_PROPERTY_NAME).lte(priceTo));
            }
        }
    }

    private static void filterByHasDiscount(Boolean hasDiscount, Query query) {
        if (hasDiscount != null) {
            if (hasDiscount) {
                query.addCriteria(AggregationExpressionCriteria.whereExpr(
                        ComparisonOperators.valueOf("$salePrice")
                                .lessThan("$regularPrice")
                ));
            } else {
                query.addCriteria(AggregationExpressionCriteria.whereExpr(
                        ComparisonOperators.valueOf("$salePrice")
                                .equalTo("$regularPrice")
                ));
            }
        }
    }

    private static void filterByInStock(Boolean inStock, Query query) {
        if (inStock != null) {
            if (inStock) {
                query.addCriteria(Criteria.where("quantityInStock").gt(0));
            } else {
                query.addCriteria(Criteria.where("quantityInStock").is(0));
            }
        }
    }

    private static void filterByCategoriesId(UUID[] categoriesId, Query query) {
        if (categoriesId != null && categoriesId.length > 0) {
            query.addCriteria(Criteria.where("categoryId").in((Object[]) categoriesId));
        }
    }

    private static void filterByTerm(String term, Query query) {
        if (StringUtils.isNotBlank(term)) {
            query.addCriteria(TextCriteria.forDefaultLanguage().matching(term));
        }
    }
}