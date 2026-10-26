package com.rafaelsousa.algashop.product.catalog.domain.model.stock;

import com.rafaelsousa.algashop.product.catalog.domain.model.DomainException;

public class DuplicateStockReservationException extends DomainException {

    public DuplicateStockReservationException(String orderId) {
        super("Reservation already exists for order " + orderId);
    }

    public DuplicateStockReservationException(String orderId, Throwable cause) {
        super("Reservation already exists for order " + orderId, cause);
    }
}
