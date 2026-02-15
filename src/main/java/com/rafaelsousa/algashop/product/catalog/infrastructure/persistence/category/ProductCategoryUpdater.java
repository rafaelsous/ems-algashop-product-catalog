package com.rafaelsousa.algashop.product.catalog.infrastructure.persistence.category;

import com.rafaelsousa.algashop.product.catalog.domain.model.category.event.CategoryUpdateEvent;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.Product;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ProductCategoryUpdater {
    private final MongoOperations mongoOperations;

    public void copyCategoryDataToProducts(CategoryUpdateEvent categoryUpdateEvent) {
        Query query = new Query(
                Criteria.where("category._id").is(categoryUpdateEvent.getCategoryId())
        );

        Update update = new Update()
                .set("category.name", categoryUpdateEvent.getName())
                .set("category.enabled", categoryUpdateEvent.getEnabled());

        mongoOperations.updateMulti(query, update, Product.class);
    }
}