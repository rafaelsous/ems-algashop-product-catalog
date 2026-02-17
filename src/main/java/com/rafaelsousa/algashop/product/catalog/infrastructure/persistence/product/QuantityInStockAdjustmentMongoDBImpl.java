package com.rafaelsousa.algashop.product.catalog.infrastructure.persistence.product;

import com.mongodb.client.result.UpdateResult;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.Product;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.QuantityInStockAdjustment;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuantityInStockAdjustmentMongoDBImpl implements QuantityInStockAdjustment {
    private final MongoOperations mongoOperations;

    @Override
    public void increase(UUID productId, int quantity) {
        Query query = new Query(Criteria.where("id").is(productId));
        changeStockQuantity(productId, quantity, query);
    }

    @Override
    public void decrease(UUID productId, int quantity) {
        Query query = new Query(Criteria.where("id")
                .is(productId)
                .and("quantityInStock").gte(quantity)
        );

        changeStockQuantity(productId, quantity * -1, query);
    }

    private void changeStockQuantity(UUID productId, int quantity, Query query) {
        Update update = new Update()
                .inc("quantityInStock", quantity)
                .inc("version", 1)
                .set("updatedAt", OffsetDateTime.now());

        UpdateResult updatedResult = mongoOperations.update(Product.class)
                .matching(query)
                .apply(update)
                .first();

        if (updatedResult.getModifiedCount() < 1) {
            throw new StockUpdateFailedException(String.format("Product of id %s was not found", productId));
        }
    }
}