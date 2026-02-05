package com.rafaelsousa.algashop.product.catalog.infrastructure.persistence.product;

import com.rafaelsousa.algashop.product.catalog.application.product.query.*;
import com.rafaelsousa.algashop.product.catalog.application.utility.Mapper;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.Product;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.ProductNotFoundException;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductQueryServiceImpl implements ProductQueryService {
    public static final String CREATED_AT_PROPERTY_NAME = "createdAt";

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

        if (filter.getEnabled() != null) {
            query.addCriteria(Criteria.where("enabled").is(filter.getEnabled()));
        }

        if (filter.getCreatedAtFrom() != null && filter.getCreatedAtTo() != null) {
            query.addCriteria(Criteria.where(CREATED_AT_PROPERTY_NAME)
                    .gte(filter.getCreatedAtFrom())
                    .lte(filter.getCreatedAtTo())
            );
        } else {
            if (filter.getCreatedAtFrom() != null) {
                query.addCriteria(Criteria.where(CREATED_AT_PROPERTY_NAME).gte(filter.getCreatedAtFrom()));
            } else if (filter.getCreatedAtTo() != null) {
                query.addCriteria(Criteria.where(CREATED_AT_PROPERTY_NAME).lte(filter.getCreatedAtTo()));
            }
        }

        return query;
    }

    private Sort sortWith(ProductFilter filter) {
        return Sort.by(filter.getSortDirectionOrDefault(), filter.getSortByPropertyOrDefault().getPropertyName());
    }
}