package com.rafaelsousa.algashop.product.catalog.domain.model.category;


import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collation = "categories")
public class Category {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    private String name;

    private Boolean enabled;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Category(String name, Boolean enabled) {
        this.setName(name);
        this.setEnabled(enabled);
        this.createdAt = OffsetDateTime.now();
    }

    public void setName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException();
        }
        
        this.name = name;
    }

    public void setEnabled(Boolean enabled) {
        Objects.requireNonNull(enabled);
        this.enabled = enabled;
    }
}