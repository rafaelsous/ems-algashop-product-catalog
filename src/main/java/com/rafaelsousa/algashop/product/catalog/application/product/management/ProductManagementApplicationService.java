package com.rafaelsousa.algashop.product.catalog.application.product.management;

import com.rafaelsousa.algashop.product.catalog.application.product.ResourceNotFoundException;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.Category;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.CategoryRepository;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.Product;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.ProductRepository;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
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

    }

    public void disable(UUID productId) {

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
                .build();
    }

    private Category findCategory(@NotNull UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(ResourceNotFoundException::new);
    }
}