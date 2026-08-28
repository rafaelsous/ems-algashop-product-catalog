package com.rafaelsousa.algashop.product.catalog.application;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface IntegrationEvent {

	@JsonIgnore
	String getAggregateId();
}
