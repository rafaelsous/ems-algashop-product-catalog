package com.rafaelsousa.algashop.product.catalog.application.utility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor@AllArgsConstructor
public class PageFilter {
    private int size = 15;
    private int page = 0;

    public static PageFilter of(int size, int page) {
        return new PageFilter(size, page);
    }
}