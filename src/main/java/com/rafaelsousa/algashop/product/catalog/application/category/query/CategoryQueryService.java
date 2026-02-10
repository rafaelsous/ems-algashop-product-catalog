package com.rafaelsousa.algashop.product.catalog.application.category.query;

import com.rafaelsousa.algashop.product.catalog.application.product.query.PageModel;

import java.util.UUID;

public interface CategoryQueryService {
    CategoryDetailOutput findById(UUID categoryId);
    PageModel<CategoryDetailOutput> filter(CategoryFilter filter);
}