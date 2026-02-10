package com.rafaelsousa.algashop.product.catalog.presentation.category;

import com.rafaelsousa.algashop.product.catalog.application.category.management.CategoryInput;
import com.rafaelsousa.algashop.product.catalog.application.category.management.CategoryManagementApplicationService;
import com.rafaelsousa.algashop.product.catalog.application.category.query.CategoryDetailOutput;
import com.rafaelsousa.algashop.product.catalog.application.category.query.CategoryFilter;
import com.rafaelsousa.algashop.product.catalog.application.category.query.CategoryQueryService;
import com.rafaelsousa.algashop.product.catalog.application.product.query.PageModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryQueryService categoryQueryService;
    private final CategoryManagementApplicationService categoryManagementApplicationService;

    @GetMapping
    public PageModel<CategoryDetailOutput> filter(CategoryFilter filter) {
        return categoryQueryService.filter(filter);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDetailOutput create(@RequestBody @Valid CategoryInput categoryInput) {
        UUID categoryId = categoryManagementApplicationService.create(categoryInput);

        return categoryQueryService.findById(categoryId);
    }

    @GetMapping("/{categoryId}")
    public CategoryDetailOutput findById(@PathVariable("categoryId") UUID categoryId) {
        return categoryQueryService.findById(categoryId);
    }

    @PutMapping("/{categoryId}")
    public CategoryDetailOutput update(@PathVariable("categoryId")UUID categoryId, @RequestBody @Valid CategoryInput categoryInput) {
        categoryManagementApplicationService.update(categoryId, categoryInput);

        return categoryQueryService.findById(categoryId);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("categoryId") UUID categoryId) {
        categoryManagementApplicationService.disable(categoryId);
    }
}