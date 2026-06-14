package com.rafaelsousa.algashop.product.catalog.presentation.product;

import com.rafaelsousa.algashop.product.catalog.application.PageModel;
import com.rafaelsousa.algashop.product.catalog.application.product.management.ProductInput;
import com.rafaelsousa.algashop.product.catalog.application.product.management.ProductManagementApplicationService;
import com.rafaelsousa.algashop.product.catalog.application.product.query.ProductDetailOutput;
import com.rafaelsousa.algashop.product.catalog.application.product.query.ProductFilter;
import com.rafaelsousa.algashop.product.catalog.application.product.query.ProductQueryService;
import com.rafaelsousa.algashop.product.catalog.application.product.query.ProductSummaryOutput;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.CategoryNotFoundException;
import com.rafaelsousa.algashop.product.catalog.infrastructure.security.SecurityAnnotations.CanReadProducts;
import com.rafaelsousa.algashop.product.catalog.infrastructure.security.SecurityAnnotations.CanWriteProductsStock;
import com.rafaelsousa.algashop.product.catalog.infrastructure.security.SecurityAnnotations.CanWriteProducts;
import com.rafaelsousa.algashop.product.catalog.presentation.ProductQuantityModel;
import com.rafaelsousa.algashop.product.catalog.presentation.UnprocessableContentException;
import jakarta.validation.Valid;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductQueryService productQueryService;
    private final ProductManagementApplicationService productManagementApplicationService;

    @GetMapping("/{productId}")
    @CanReadProducts
    public ResponseEntity<ProductDetailOutput> findById(@PathVariable UUID productId) {
	    ProductDetailOutput product = productQueryService.findById(productId);

        return ResponseEntity.ok()
		    .cacheControl(CacheControl.maxAge(Duration.ofMinutes(1)).cachePublic())
		    .eTag("product:id:" + product.getId() + ":v:" + product.getVersion())
		    .lastModified(product.getUpdatedAt().toInstant())
		    .body(product);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CanWriteProducts
    public ProductDetailOutput create(@RequestBody @Valid ProductInput productInput) {
        try {
            return productManagementApplicationService.create(productInput);
        } catch (CategoryNotFoundException ex) {
            throw new UnprocessableContentException(ex.getMessage(), ex);
        }
    }

    @GetMapping
    @CanReadProducts
    public PageModel<ProductSummaryOutput> filter(ProductFilter filter) {
        return productQueryService.filter(filter);
    }

    @PutMapping("/{productId}")
    @CanWriteProducts
    public ProductDetailOutput update(@PathVariable UUID productId, @RequestBody @Valid ProductInput productInput) {
        return productManagementApplicationService.update(productId, productInput);
    }

    @PutMapping("/{productId}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CanWriteProducts
    public void enable(@PathVariable UUID productId) {
        productManagementApplicationService.enable(productId);
    }

    @DeleteMapping("/{productId}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CanWriteProducts
    public void disable(@PathVariable UUID productId) {
        productManagementApplicationService.disable(productId);
    }

	@PostMapping("/{productId}/restock")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@CanWriteProductsStock
	public void restock(@PathVariable UUID productId, @RequestBody @Valid ProductQuantityModel productQuantityModel) {
		productManagementApplicationService.restock(productId, productQuantityModel.getQuantity());
	}

	@PostMapping("/{productId}/withdraw")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@CanWriteProductsStock
	public void withdraw(@PathVariable UUID productId, @RequestBody @Valid ProductQuantityModel productQuantityModel) {
		productManagementApplicationService.withdraw(productId, productQuantityModel.getQuantity());
	}
}