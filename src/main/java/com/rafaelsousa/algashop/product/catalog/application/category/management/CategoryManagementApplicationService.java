package com.rafaelsousa.algashop.product.catalog.application.category.management;

import com.rafaelsousa.algashop.product.catalog.application.ApplicationMessagePublisher;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.Category;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.CategoryNotFoundException;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.CategoryRepository;
import java.util.UUID;

import com.rafaelsousa.algashop.product.catalog.domain.model.category.event.CategoryUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryManagementApplicationService {
    private final CategoryRepository categoryRepository;
    private final ApplicationMessagePublisher applicationMessagePublisher;

	@CacheEvict(value = "algashop:categories-filter:v1", key = "'default'")
    public UUID create(CategoryInput categoryInput) {
        Category category = new Category(categoryInput.getName(), categoryInput.getEnabled());
        categoryRepository.save(category);

        return category.getId();
    }

	@Caching(evict = {
		@CacheEvict(value = "algashop:categories-filter:v1", key = "'default'"),
		@CacheEvict(value = "algashop:categories:v1", key = "#categoryId")
	})
    public void update(UUID categoryId, CategoryInput categoryInput) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        category.setName(categoryInput.getName());
        category.setEnabled(categoryInput.getEnabled());

        categoryRepository.save(category);

        applicationMessagePublisher.send(new CategoryUpdateEvent(category.getId(),
                category.getName(), category.getEnabled()));
    }

    public void disable(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        category.setEnabled(false);

        categoryRepository.save(category);

        applicationMessagePublisher.send(new CategoryUpdateEvent(category.getId(),
                category.getName(), category.getEnabled()));
    }
}