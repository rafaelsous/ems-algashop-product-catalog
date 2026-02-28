package com.rafaelsousa.algashop.product.catalog.presentation.product;

import com.rafaelsousa.algashop.product.catalog.application.PageModel;
import com.rafaelsousa.algashop.product.catalog.application.product.management.ProductInput;
import com.rafaelsousa.algashop.product.catalog.application.product.management.ProductManagementApplicationService;
import com.rafaelsousa.algashop.product.catalog.application.product.query.*;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.CategoryNotFoundException;
import com.rafaelsousa.algashop.product.catalog.presentation.ProductQuantityModel;
import com.rafaelsousa.algashop.product.catalog.presentation.UnprocessableContentException;
import jakarta.validation.Valid;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductQueryService productQueryService;
    private final ProductManagementApplicationService productManagementApplicationService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailOutput> findById(@PathVariable("productId") UUID productId) {
	    ProductDetailOutput product = productQueryService.findById(productId);

    return ResponseEntity.ok()
		    .cacheControl(CacheControl.maxAge(Duration.ofMinutes(1)).cachePublic())
		    .eTag("product:id:" + product.getId() + ":v:" + product.getVersion())
		    .lastModified(product.getUpdatedAt().toInstant())
		    .body(product);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDetailOutput create(@RequestBody @Valid ProductInput productInput) {
        try {
            return productManagementApplicationService.create(productInput);
        } catch (CategoryNotFoundException ex) {
            throw new UnprocessableContentException(ex.getMessage(), ex);
        }
    }

    @GetMapping
    public PageModel<ProductSummaryOutput> filter(ProductFilter filter) {
        return productQueryService.filter(filter);
    }

    @PutMapping("/{productId}")
    public ProductDetailOutput update(@PathVariable("productId") UUID productId, @RequestBody @Valid ProductInput productInput) {
        return productManagementApplicationService.update(productId, productInput);
    }

    @PutMapping("/{productId}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enable(@PathVariable("productId") UUID productId) {
        productManagementApplicationService.enable(productId);
    }

    @DeleteMapping("/{productId}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disable(@PathVariable("productId") UUID productId) {
        productManagementApplicationService.disable(productId);
    }

	@PostMapping("/{productId}/restock")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void restock(@PathVariable("productId") UUID productId,
	                    @RequestBody @Valid ProductQuantityModel productQuantityModel) {
		productManagementApplicationService.restock(productId, productQuantityModel.getQuantity());
	}

	@PostMapping("/{productId}/withdraw")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void withdraw(@PathVariable("productId") UUID productId,
	                    @RequestBody @Valid ProductQuantityModel productQuantityModel) {
		productManagementApplicationService.withdraw(productId, productQuantityModel.getQuantity());
	}
}