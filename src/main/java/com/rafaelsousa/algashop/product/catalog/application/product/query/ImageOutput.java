package com.rafaelsousa.algashop.product.catalog.application.product.query;

import lombok.Data;

import java.util.UUID;

@Data
public class ImageOutput {
	private UUID id;
	private String url;
}