package com.rafaelsousa.algashop.product.catalog.application.product.management;

import com.rafaelsousa.algashop.product.catalog.application.product.query.ProductDetailOutput;
import com.rafaelsousa.algashop.product.catalog.application.utility.Mapper;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.Category;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.CategoryNotFoundException;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.CategoryRepository;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.*;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductManagementApplicationService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
	private final StockMovementRepository stockMovementRepository;

	private final Mapper mapper;
	private final StockService stockService;

	@CachePut(cacheNames = "algashop:products:v1", key = "#result.id", condition = "#productInput.enabled == true")
    public ProductDetailOutput create(ProductInput productInput) {
        Product product = mapToProduct(productInput);
        productRepository.save(product);

        return mapper.convert(product, ProductDetailOutput.class);
    }

	@CachePut(cacheNames = "algashop:products:v1", key = "#result.id", condition = "#productInput.enabled == true")
    public ProductDetailOutput update(UUID productId, ProductInput productInput) {
        Product product = findProduct(productId);
        Category category = findCategory(productInput.getCategoryId());

        updateProduct(product, productInput);
        product.setCategory(category);

        productRepository.save(product);

		return mapper.convert(product, ProductDetailOutput.class);
    }

    public void disable(UUID productId) {
        Product product = findProduct(productId);
        product.disable();

        productRepository.save(product);
    }

    public void enable(UUID productId) {
        Product product = findProduct(productId);
        product.enable();

        productRepository.save(product);
    }

	@Transactional
	public void restock(UUID productId, Integer quantity) {
		Product product = findProduct(productId);

		StockMovement stockMovement = stockService.restock(product, quantity);
		stockMovementRepository.save(stockMovement);
	}

	@Transactional
	public void withdraw(UUID productId, Integer quantity) {
		Product product = findProduct(productId);

		StockMovement stockMovement = stockService.withdraw(product, quantity);
		stockMovementRepository.save(stockMovement);
	}

    private Product mapToProduct(ProductInput productInput) {
        Category category = findCategory(productInput.getCategoryId());

        return Product.builder()
                .name(productInput.getName())
                .brand(productInput.getBrand())
                .description(productInput.getDescription())
                .enabled(productInput.getEnabled())
                .regularPrice(productInput.getRegularPrice())
                .salePrice(productInput.getSalePrice())
                .category(category)
                .build();
    }

    private Category findCategory(@NotNull UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    private Product findProduct(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    private void updateProduct(Product product, ProductInput productInput) {
        product.setName(productInput.getName());
        product.setBrand(productInput.getBrand());
        product.setDescription(productInput.getDescription());
        product.setEnabled(productInput.getEnabled());

        product.changePrice(productInput.getRegularPrice(), productInput.getSalePrice());
    }
}