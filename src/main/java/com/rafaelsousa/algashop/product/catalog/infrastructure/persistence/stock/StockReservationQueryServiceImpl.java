package com.rafaelsousa.algashop.product.catalog.infrastructure.persistence.stock;

import com.rafaelsousa.algashop.product.catalog.application.stock.query.StockReservationOutput;
import com.rafaelsousa.algashop.product.catalog.application.stock.query.StockReservationQueryService;
import com.rafaelsousa.algashop.product.catalog.domain.model.stock.StockReservation;
import com.rafaelsousa.algashop.product.catalog.domain.model.stock.StockReservationNotFoundException;
import com.rafaelsousa.algashop.product.catalog.domain.model.stock.StockReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockReservationQueryServiceImpl implements StockReservationQueryService {
	private final StockReservationRepository stockReservationRepository;

	@Override
	public StockReservationOutput findById(UUID stockReservationId) {
		StockReservation stockReservation = stockReservationRepository.findById(stockReservationId)
			.orElseThrow(() -> new StockReservationNotFoundException(stockReservationId));

		return StockReservationOutput.builder()
			.id(stockReservation.getId())
			.orderId(stockReservation.getOrderId())
			.confirmed(stockReservation.isConfirmed())
			.createdAt(stockReservation.getCreatedAt())
			.build();
	}
}
