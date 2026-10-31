package com.rafaelsousa.algashop.product.catalog.domain.model.stock;

import com.rafaelsousa.algashop.product.catalog.domain.model.DomainEntityNotFoundException;

import java.util.UUID;

public class StockReservationNotFoundException
        extends DomainEntityNotFoundException {
    public StockReservationNotFoundException(UUID reservationId) {
        super(String.format("Stock reservation with id %s was not found", reservationId));
    }
}
