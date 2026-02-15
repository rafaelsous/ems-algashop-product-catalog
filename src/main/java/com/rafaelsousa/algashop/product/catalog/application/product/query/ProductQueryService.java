package com.rafaelsousa.algashop.product.catalog.application.product.query;

import com.rafaelsousa.algashop.product.catalog.application.PageModel;

import java.util.UUID;

public interface ProductQueryService {
    ProductDetailOutput findById(UUID productId);
    PageModel<ProductSummaryOutput> filter(ProductFilter filter);
}