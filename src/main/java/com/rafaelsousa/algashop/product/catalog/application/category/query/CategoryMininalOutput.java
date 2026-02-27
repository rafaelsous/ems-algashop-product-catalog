package com.rafaelsousa.algashop.product.catalog.application.category.query;

import com.rafaelsousa.algashop.product.catalog.infrastructure.utility.Slugfier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMininalOutput implements Serializable {
    private UUID id;
    private String name;
    private Boolean enabled;

    public String getSlug() {
        return Slugfier.slugify(this.getName());
    }
}