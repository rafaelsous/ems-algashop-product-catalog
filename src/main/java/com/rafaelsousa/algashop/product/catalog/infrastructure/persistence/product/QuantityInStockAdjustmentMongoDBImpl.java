package com.rafaelsousa.algashop.product.catalog.infrastructure.persistence.product;

import com.rafaelsousa.algashop.product.catalog.domain.model.product.Product;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.ProductNotFoundException;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.QuantityInStockAdjustment;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuantityInStockAdjustmentMongoDBImpl implements QuantityInStockAdjustment {
    public static final String QUANTITY_IN_STOCK_PROPERTY_NAME = "quantityInStock";

    private final MongoOperations mongoOperations;

    @Override
    public Result increase(UUID productId, int quantity) {
        Query query = new Query(Criteria.where("id").is(productId));

        return changeStockQuantity(productId, quantity, query);
    }

    @Override
    public Result decrease(UUID productId, int quantity) {
        Query query = new Query(Criteria.where("id")
                .is(productId)
                .and(QUANTITY_IN_STOCK_PROPERTY_NAME).gte(quantity)
        );

        return changeStockQuantity(productId, quantity * -1, query);
    }

    private Result changeStockQuantity(UUID productId, int quantity, Query queryForUpdate) {
        Aggregation findProductQuantity = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("id").is(productId)),
                Aggregation.project(QUANTITY_IN_STOCK_PROPERTY_NAME)
        );

        Document productBeforeUpdate = mongoOperations
                .aggregate(findProductQuantity, Product.class, Document.class).getUniqueMappedResult();

        if (productBeforeUpdate == null) {
            throw new ProductNotFoundException(productId);
        }

        Integer previousQuantity = productBeforeUpdate.getInteger(QUANTITY_IN_STOCK_PROPERTY_NAME);

        Update update = new Update()
                .inc(QUANTITY_IN_STOCK_PROPERTY_NAME, quantity)
                .inc("version", 1)
                .set("updatedAt", OffsetDateTime.now());

        Product updatedProduct = mongoOperations.findAndModify(queryForUpdate, update, new FindAndModifyOptions()
                .returnNew(true), Product.class);

        if (updatedProduct == null) {
            throw new StockUpdateFailedException(String.format("Failed to update stock of product %s", productId));
        }

      Integer newQuantity = updatedProduct.getQuantityInStock();

      return new Result(productId, previousQuantity, newQuantity);
    }
}