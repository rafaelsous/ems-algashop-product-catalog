package com.rafaelsousa.algashop.product.catalog.presentation.category;

import com.rafaelsousa.algashop.product.catalog.application.PageModel;
import com.rafaelsousa.algashop.product.catalog.application.category.management.CategoryInput;
import com.rafaelsousa.algashop.product.catalog.application.category.management.CategoryManagementApplicationService;
import com.rafaelsousa.algashop.product.catalog.application.category.query.CategoryDetailOutput;
import com.rafaelsousa.algashop.product.catalog.application.category.query.CategoryFilter;
import com.rafaelsousa.algashop.product.catalog.application.category.query.CategoryQueryService;
import com.rafaelsousa.algashop.product.catalog.infrastructure.security.check.SecurityAnnotations.CanReadCategories;
import com.rafaelsousa.algashop.product.catalog.infrastructure.security.check.SecurityAnnotations.CanWriteCategories;
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryQueryService categoryQueryService;
    private final CategoryManagementApplicationService categoryManagementApplicationService;

    @GetMapping
    @CanReadCategories
    public ResponseEntity<PageModel<CategoryDetailOutput>> filter(CategoryFilter filter, WebRequest webRequest) {
	    if (!filter.isCacheable()) {
		    PageModel<CategoryDetailOutput> result = categoryQueryService.filter(filter);
		    return ResponseEntity.ok(result);
	    }

		OffsetDateTime lastModified = categoryQueryService.lastModified();

	    if (webRequest.checkNotModified(lastModified.toInstant().toEpochMilli())) {
		    return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
	    }

	    PageModel<CategoryDetailOutput> result = categoryQueryService.filter(filter);

	    return ResponseEntity.ok()
			    .cacheControl(CacheControl.maxAge(Duration.ofMinutes(5)).cachePublic())
			    .lastModified(lastModified.toInstant())
			    .body(result);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CanWriteCategories
    public CategoryDetailOutput create(@RequestBody @Valid CategoryInput categoryInput) {
        UUID categoryId = categoryManagementApplicationService.create(categoryInput);

        return categoryQueryService.findById(categoryId);
    }

    @GetMapping("/{categoryId}")
    @CanReadCategories
    public ResponseEntity<CategoryDetailOutput> findById(@PathVariable("categoryId") UUID categoryId) {
	    CategoryDetailOutput categoryDetailOutput = categoryQueryService.findById(categoryId);

		return ResponseEntity.ok()
				.cacheControl(CacheControl.maxAge(Duration.ofMinutes(5)).cachePublic())
			    .eTag("category:id:" + categoryDetailOutput.getId() + ":v:" + categoryDetailOutput.getVersion())
			    .lastModified(categoryDetailOutput.getUpdatedAt().toInstant())
			    .body(categoryDetailOutput);
    }

    @PutMapping("/{categoryId}")
    @CanWriteCategories
    public CategoryDetailOutput update(@PathVariable("categoryId")UUID categoryId, @RequestBody @Valid CategoryInput categoryInput) {
        categoryManagementApplicationService.update(categoryId, categoryInput);

        return categoryQueryService.findById(categoryId);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CanWriteCategories
    public void delete(@PathVariable("categoryId") UUID categoryId) {
        categoryManagementApplicationService.disable(categoryId);
    }
}