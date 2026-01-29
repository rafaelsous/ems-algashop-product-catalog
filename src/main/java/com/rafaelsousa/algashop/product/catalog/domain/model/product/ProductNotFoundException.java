package com.rafaelsousa.algashop.product.catalog.domain.model.product;

import com.rafaelsousa.algashop.product.catalog.domain.model.DomainEntityNotFoundException;

import java.util.UUID;

public class ProductNotFoundException extends DomainEntityNotFoundException {

    public ProductNotFoundException(UUID productId) {
        super(String.format("Product with id %s was not found", productId));
    }
}