package com.rafaelsousa.algashop.product.catalog.application.stock.management;

import com.rafaelsousa.algashop.product.catalog.domain.model.product.ProductNotFoundException;
import com.rafaelsousa.algashop.product.catalog.domain.model.stock.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StockReservationApplicationService {
    private final StockService stockService;
    private final StockMovementRepository stockMovementRepository;
    private final StockReservationRepository stockReservationRepository;

    @Transactional
    public void reserveForOrder(StockReservationInput stockReservationInput) {
        Objects.requireNonNull(stockReservationInput);

        String orderId = stockReservationInput.getOrderId();
        assertReservationDoesNotExist(orderId);

        List<StockReservationItem> items = toItems(stockReservationInput.getItems());

        List<StockMovement> movements;
        StockReservation stockReservation;
        try {
            movements = stockService.withdrawAll(orderId, items);
            stockReservation = StockReservation.confirmed(orderId, items);
        } catch (InsufficientStockException | ProductNotFoundException ex) {
            movements = List.of();
            stockReservation = StockReservation.rejected(orderId, items);
        }

        if (!movements.isEmpty()) {
            stockMovementRepository.saveAll(movements);
        }

        stockReservationRepository.save(stockReservation);
    }

    private void assertReservationDoesNotExist(String orderId) {
        if (stockReservationRepository.existsByOrderId(orderId)) {
            throw new DuplicateStockReservationException(orderId);
        }
    }

    private List<StockReservationItem> toItems(List<StockReservationItemInput> items) {
        return items.stream().map(this::toItem).toList();
    }

    private StockReservationItem toItem(StockReservationItemInput item) {
        return StockReservationItem.builder()
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .build();
    }
}
