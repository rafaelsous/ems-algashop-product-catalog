package com.rafaelsousa.algashop.product.catalog.domain.model.product;

import com.rafaelsousa.algashop.product.catalog.infrastructure.persistence.config.MongoConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Slf4j
@DataMongoTest
@Import(MongoConfig.class)
class ProductRepositoryIT {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Test
    void shouldFilter() {
    Page<ProductProjection> products = productRepository
            .findAllByEnabled(true, PageRequest.of(0, 2));
        products.forEach(product -> log.info("Product -> ID: {} - Name: {}", product.id(), product.name()));
    }
}