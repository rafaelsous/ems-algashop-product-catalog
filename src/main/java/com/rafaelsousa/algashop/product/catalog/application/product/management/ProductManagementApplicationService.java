package com.rafaelsousa.algashop.product.catalog.application.product.management;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductManagementApplicationService {

    public UUID create(ProductInput productInput) {
        return UUID.randomUUID();
    }

    public void update(UUID productId, ProductInput productInput) {

    }

    public void disable(UUID productId) {

    }
}