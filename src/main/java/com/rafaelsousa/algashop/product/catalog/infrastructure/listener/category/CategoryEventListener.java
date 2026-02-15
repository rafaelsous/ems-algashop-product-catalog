package com.rafaelsousa.algashop.product.catalog.infrastructure.listener.category;

import com.rafaelsousa.algashop.product.catalog.domain.model.category.event.CategoryUpdateEvent;
import com.rafaelsousa.algashop.product.catalog.infrastructure.persistence.category.ProductCategoryUpdater;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class CategoryEventListener {
    private final ProductCategoryUpdater productCategoryUpdater;

    @Async
    @EventListener
    public void handleCategoryUpdateEvent(CategoryUpdateEvent event) {
        log.info("Received CategoryUpdateEvent: {}", event.getCategoryId());
        productCategoryUpdater.copyCategoryDataToProducts(event);
    }
}