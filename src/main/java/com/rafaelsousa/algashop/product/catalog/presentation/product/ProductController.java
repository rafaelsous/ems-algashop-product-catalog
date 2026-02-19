package com.rafaelsousa.algashop.product.catalog.presentation.product;

import com.rafaelsousa.algashop.product.catalog.application.PageModel;
import com.rafaelsousa.algashop.product.catalog.application.product.management.ProductInput;
import com.rafaelsousa.algashop.product.catalog.application.product.management.ProductManagementApplicationService;
import com.rafaelsousa.algashop.product.catalog.application.product.query.*;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.CategoryNotFoundException;
import com.rafaelsousa.algashop.product.catalog.presentation.ProductQuantityModel;
import com.rafaelsousa.algashop.product.catalog.presentation.UnprocessableContentException;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductQueryService productQueryService;
    private final ProductManagementApplicationService productManagementApplicationService;

    @GetMapping("/{productId}")
    public ProductDetailOutput findById(@PathVariable("productId") UUID productId) {
        return productQueryService.findById(productId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDetailOutput create(@RequestBody @Valid ProductInput productInput) {
        UUID productId;

        try {
            productId = productManagementApplicationService.create(productInput);
        } catch (CategoryNotFoundException ex) {
            throw new UnprocessableContentException(ex.getMessage(), ex);
        }

        return productQueryService.findById(productId);
    }

    @GetMapping
    public PageModel<ProductSummaryOutput> filter(ProductFilter filter) {
        return productQueryService.filter(filter);
    }

    @PutMapping("/{productId}")
    public ProductDetailOutput update(@PathVariable("productId") UUID productId, @RequestBody @Valid ProductInput productInput) {
        productManagementApplicationService.update(productId, productInput);

        return productQueryService.findById(productId);
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