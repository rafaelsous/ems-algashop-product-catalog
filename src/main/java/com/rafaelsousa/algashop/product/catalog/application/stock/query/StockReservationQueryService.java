package com.rafaelsousa.algashop.product.catalog.application.stock.query;

import java.util.UUID;

public interface StockReservationQueryService {
	StockReservationOutput findById(UUID stockReservationId);
}
