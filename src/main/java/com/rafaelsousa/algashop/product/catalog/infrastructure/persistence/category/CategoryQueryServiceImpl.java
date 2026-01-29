package com.rafaelsousa.algashop.product.catalog.infrastructure.persistence.category;

import com.rafaelsousa.algashop.product.catalog.application.category.query.CategoryDetailOutput;
import com.rafaelsousa.algashop.product.catalog.application.category.query.CategoryQueryService;
import com.rafaelsousa.algashop.product.catalog.application.product.query.PageModel;
import com.rafaelsousa.algashop.product.catalog.application.utility.Mapper;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.Category;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.CategoryNotFoundException;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.CategoryRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryQueryServiceImpl implements CategoryQueryService {
    private final CategoryRepository categoryRepository;
    private final Mapper mapper;

    @Override
    public CategoryDetailOutput findById(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        return mapper.convert(category, CategoryDetailOutput.class);
    }

    @Override
    public PageModel<CategoryDetailOutput> filter(Integer size, Integer page) {
        return null;
    }
}