package com.rafaelsousa.algashop.product.catalog.presentation;

import com.rafaelsousa.algashop.product.catalog.application.product.management.ProductInput;
import com.rafaelsousa.algashop.product.catalog.application.product.management.ProductManagementApplicationService;
import com.rafaelsousa.algashop.product.catalog.application.product.query.PageModel;
import com.rafaelsousa.algashop.product.catalog.application.product.query.ProductDetailOutput;
import com.rafaelsousa.algashop.product.catalog.application.product.query.ProductQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
        UUID productId = productManagementApplicationService.create(productInput);

        return productQueryService.findById(productId);
    }

    @GetMapping
    public PageModel<ProductDetailOutput> filter(
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "number", required = false) Integer number
            ) {
        return productQueryService.filter(size, number);
    }

    @PutMapping("/{productId}")
    public ProductDetailOutput update(@PathVariable("productId") UUID productId, @RequestBody @Valid ProductInput productInput) {
        productManagementApplicationService.update(productId, productInput);

        return productQueryService.findById(productId);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("productId") UUID productId) {
        productManagementApplicationService.disable(productId);
    }
}