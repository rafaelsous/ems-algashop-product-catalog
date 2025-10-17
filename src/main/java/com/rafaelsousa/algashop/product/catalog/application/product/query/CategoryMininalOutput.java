package com.rafaelsousa.algashop.product.catalog.application.product.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMininalOutput {
    private UUID id;
    private String name;
}