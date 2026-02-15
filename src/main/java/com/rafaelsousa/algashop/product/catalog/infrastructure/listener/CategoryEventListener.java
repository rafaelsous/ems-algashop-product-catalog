package com.rafaelsousa.algashop.product.catalog.infrastructure.listener;

import com.rafaelsousa.algashop.product.catalog.domain.model.category.event.CategoryUpdateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CategoryEventListener {

    @EventListener
    public void handleCategoryUpdateEvent(CategoryUpdateEvent event) {
        log.info("Received CategoryUpdateEvent: {}", event.getCategoryId());
    }
}