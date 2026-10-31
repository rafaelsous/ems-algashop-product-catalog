package com.rafaelsousa.algashop.product.catalog.application.stock.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservationOutput {
    private UUID id;
    private String orderId;
    private boolean confirmed;
    private OffsetDateTime createdAt;
}
