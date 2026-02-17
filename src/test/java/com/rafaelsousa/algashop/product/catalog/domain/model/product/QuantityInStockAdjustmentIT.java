package com.rafaelsousa.algashop.product.catalog.domain.model.product;

import com.rafaelsousa.algashop.product.catalog.domain.model.product.QuantityInStockAdjustment.Result;
import com.rafaelsousa.algashop.product.catalog.infrastructure.persistence.config.MongoConfig;
import com.rafaelsousa.algashop.product.catalog.infrastructure.persistence.dataload.DataLoadProperties;
import com.rafaelsousa.algashop.product.catalog.infrastructure.persistence.dataload.DataLoader;
import com.rafaelsousa.algashop.product.catalog.infrastructure.persistence.product.QuantityInStockAdjustmentMongoDBImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DataMongoTest
@Import({
		MongoConfig.class,
		DataLoader.class,
		DataLoadProperties.class,
		QuantityInStockAdjustmentMongoDBImpl.class
})
class QuantityInStockAdjustmentIT {
	private static final UUID EXISTING_PRODUCT_ID = UUID.fromString("946cea3b-d11d-4f11-b88d-3089b4e74087");

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private QuantityInStockAdjustment quantityInStockAdjustment;

	@Autowired
	private DataLoader dataLoader;

	@BeforeEach
	void setUp() {
		dataLoader.run(new DefaultApplicationArguments());
	}

    @Test
    void shouldIncreaseQuantity() {
        Product product = productRepository.findById(EXISTING_PRODUCT_ID).orElseThrow();

        quantityInStockAdjustment.increase(EXISTING_PRODUCT_ID, 25);
        quantityInStockAdjustment.increase(EXISTING_PRODUCT_ID, 25);

        Product updatedProduct = productRepository.findById(EXISTING_PRODUCT_ID).orElseThrow();

	    assertThat(product.getQuantityInStock()).isEqualTo(50);

		int expectedQuantity = product.getQuantityInStock() + 50;
		assertThat(updatedProduct.getQuantityInStock()).isEqualTo(expectedQuantity);
    }

    @Test
    void shouldDecreaseQuantity() {
        Product product = productRepository.findById(EXISTING_PRODUCT_ID).orElseThrow();

        quantityInStockAdjustment.decrease(EXISTING_PRODUCT_ID, 25);
        quantityInStockAdjustment.decrease(EXISTING_PRODUCT_ID, 25);

        Product updatedProduct = productRepository.findById(EXISTING_PRODUCT_ID).orElseThrow();

        assertThat(product.getQuantityInStock()).isEqualTo(50);

        int expectedQuantity = product.getQuantityInStock() - 50;
        assertThat(updatedProduct.getQuantityInStock()).isEqualTo(expectedQuantity);
        assertThat(updatedProduct.isInStock()).isFalse();
    }

    @Test
    void shouldNotDecreaseQuantity() {
		assertThatExceptionOfType(RuntimeException.class)
		    .isThrownBy(() -> quantityInStockAdjustment.decrease(EXISTING_PRODUCT_ID, 100));

		Product product = productRepository.findById(EXISTING_PRODUCT_ID).orElseThrow();
		assertThat(product.getQuantityInStock()).isEqualTo(50);
	}

    @Test
    void shouldCalculateResult() {
	    Result result = quantityInStockAdjustment.decrease(EXISTING_PRODUCT_ID, 40);

	    assertThat(result.previousQuantity()).isEqualTo(50);
	    assertThat(result.newQuantity()).isEqualTo(10);
    }
}