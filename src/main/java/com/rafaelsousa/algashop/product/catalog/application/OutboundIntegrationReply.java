package com.rafaelsousa.algashop.product.catalog.application;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface OutboundIntegrationReply {

	@JsonIgnore
	String getAggregateId();
}
