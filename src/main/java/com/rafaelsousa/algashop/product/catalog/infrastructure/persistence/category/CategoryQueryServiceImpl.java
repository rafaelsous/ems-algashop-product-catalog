package com.rafaelsousa.algashop.product.catalog.infrastructure.persistence.category;

import com.rafaelsousa.algashop.product.catalog.application.category.query.CategoryDetailOutput;
import com.rafaelsousa.algashop.product.catalog.application.category.query.CategoryQueryService;
import com.rafaelsousa.algashop.product.catalog.application.product.ResourceNotFoundException;
import com.rafaelsousa.algashop.product.catalog.application.product.query.PageModel;
import com.rafaelsousa.algashop.product.catalog.application.utility.Mapper;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.Category;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryQueryServiceImpl implements CategoryQueryService {
    private final CategoryRepository categoryRepository;
    private final Mapper mapper;

    @Override
    public CategoryDetailOutput findById(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(ResourceNotFoundException::new);

        return mapper.convert(category, CategoryDetailOutput.class);
    }

    @Override
    public PageModel<CategoryDetailOutput> filter(Integer size, Integer page) {
        return null;
    }
}