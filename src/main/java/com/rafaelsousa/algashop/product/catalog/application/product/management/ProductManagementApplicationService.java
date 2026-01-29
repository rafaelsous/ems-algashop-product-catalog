package com.rafaelsousa.algashop.product.catalog.application.product.management;

import com.rafaelsousa.algashop.product.catalog.domain.model.category.Category;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.CategoryNotFoundException;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.CategoryRepository;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.Product;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.ProductNotFoundException;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.ProductRepository;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductManagementApplicationService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public UUID create(ProductInput productInput) {
        Product product = mapToProduct(productInput);
        productRepository.save(product);

        return product.getId();
    }

    public void update(UUID productId, ProductInput productInput) {
        Product product = findProduct(productId);
        Category category = findCategory(productInput.getCategoryId());

        updateProduct(product, productInput);
        product.setCategory(category);

        productRepository.save(product);
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
        product.setRegularPrice(productInput.getRegularPrice());
        product.setSalePrice(productInput.getSalePrice());
    }
}