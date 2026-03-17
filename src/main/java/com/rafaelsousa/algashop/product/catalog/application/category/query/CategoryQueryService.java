package com.rafaelsousa.algashop.product.catalog.application.category.query;

import com.rafaelsousa.algashop.product.catalog.application.PageModel;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface CategoryQueryService {
    CategoryDetailOutput findById(UUID categoryId);
    PageModel<CategoryDetailOutput> filter(CategoryFilter filter);
	OffsetDateTime lastModified();
}