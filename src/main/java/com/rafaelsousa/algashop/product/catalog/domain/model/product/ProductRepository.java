package com.rafaelsousa.algashop.product.catalog.domain.model.product;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ProductRepository extends MongoRepository<Product, UUID> {

    @Query(value = "{'enabled': ?0}", fields = "{'name': 1}")
    Page<ProductProjection> findAllByEnabled(Boolean enabled, Pageable pageable);
}