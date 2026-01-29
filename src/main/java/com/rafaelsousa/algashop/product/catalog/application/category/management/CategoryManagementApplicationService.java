package com.rafaelsousa.algashop.product.catalog.application.category.management;

import com.rafaelsousa.algashop.product.catalog.domain.model.category.Category;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.CategoryNotFoundException;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.CategoryRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryManagementApplicationService {
    private final CategoryRepository categoryRepository;

    public UUID create(CategoryInput categoryInput) {
        Category category = new Category(categoryInput.getName(), categoryInput.getEnabled());
        categoryRepository.save(category);

        return category.getId();
    }

    public void update(UUID categoryId, CategoryInput categoryInput) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        category.setName(categoryInput.getName());
        category.setEnabled(categoryInput.getEnabled());

        categoryRepository.save(category);
    }

    public void disable(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        category.setEnabled(false);

        categoryRepository.save(category);
    }
}