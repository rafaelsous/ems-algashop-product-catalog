package com.rafaelsousa.algashop.product.catalog.infrastructure.security;

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
	@PreAuthorize("hasAuthority('SCOPE_products:write')")
	public @interface CanWriteProducts {}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.TYPE})
	@PreAuthorize("hasAuthority('SCOPE_products:stock:write')")
	public @interface CanWriteProductsStock {}
}