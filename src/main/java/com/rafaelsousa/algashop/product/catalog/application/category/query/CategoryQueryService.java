package com.rafaelsousa.algashop.product.catalog.application.category.query;

import com.rafaelsousa.algashop.product.catalog.application.product.query.PageModel;

import java.util.UUID;

public interface CategoryQueryService {
    CategoryDetailOutput findById(UUID productId);
    PageModel<CategoryDetailOutput> filter(Integer size, Integer page);
}