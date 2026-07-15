package com.rafaelsousa.algashop.product.catalog.infrastructure.security.check;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class SecurityAnnotations {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})
    @PreAuthorize("hasAuthority('SCOPE_products:read')")
    public @interface CanReadProducts {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})
    @PreAuthorize("hasAuthority('SCOPE_products:write') and not hasRole('CUSTOMER')")
    public @interface CanWriteProducts {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})
    @PreAuthorize("hasAuthority('SCOPE_products:stock:write') and hasRole('MANAGER')")
    public @interface CanWriteProductsStock {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})
    @PreAuthorize("hasAuthority('SCOPE_categories:read')")
    public @interface CanReadCategories {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})
    @PreAuthorize("hasAuthority('SCOPE_categories:write') and not hasRole('CUSTOMER')")
    public @interface CanWriteCategories {}
}
