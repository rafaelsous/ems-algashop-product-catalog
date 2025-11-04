package com.rafaelsousa.algashop.product.catalog.application.category.management;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CategoryManagementApplicationService {

    public UUID create(CategoryInput categoryInput) {
        return UUID.randomUUID();
    }

    public void update(UUID categoryId, CategoryInput categoryInput) {

    }

    public void disable(UUID categoryId) {

    }
}