package com.rafaelsousa.algashop.product.catalog.presentation.product;

import com.rafaelsousa.algashop.product.catalog.application.product.management.ImageInput;
import com.rafaelsousa.algashop.product.catalog.application.product.management.ProductImageManagementApplicationService;
import com.rafaelsousa.algashop.product.catalog.application.product.query.ImageOutput;
import com.rafaelsousa.algashop.product.catalog.application.product.query.ProductImageQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/{productId}/images")
@RequiredArgsConstructor
public class ProductImagesController {
	private final ProductImageQueryService productImageQueryService;
	private final ProductImageManagementApplicationService managementService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ImageOutput create(@PathVariable UUID productId, @RequestBody @Valid ImageInput input) {
        return managementService.create(productId, input);
    }

    @DeleteMapping("{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID productId, @PathVariable UUID imageId) {
        managementService.delete(productId, imageId);
    }

    @PutMapping("{imageId}/primary")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void primary(@PathVariable UUID productId, @PathVariable UUID imageId) {
        managementService.primary(productId, imageId);
    }

	@GetMapping
	public List<ImageOutput> getAll(@PathVariable UUID productId) {
		return productImageQueryService.getAllImages(productId);
	}

	@GetMapping("{imageId}")
	public ImageOutput getOne(@PathVariable UUID productId, @PathVariable UUID imageId) {
		return productImageQueryService.getImage(productId, imageId);
	}
}