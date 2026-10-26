package com.rafaelsousa.algashop.product.catalog.domain.model.stock;

import com.rafaelsousa.algashop.product.catalog.domain.model.IdGenerator;
import com.rafaelsousa.algashop.product.catalog.domain.model.stock.event.StockMovementRegisteredEvent;

import lombok.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Document(collection = "stock_movements")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockMovement extends AbstractAggregateRoot<StockMovement> {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    private OffsetDateTime occurredAt;
    private UUID productId;
    private Integer movementQuantity;
    private Integer previousQuantity;
    private Integer newQuantity;
    private MovementType type;
    private String orderId;

    @Builder
    public StockMovement(UUID productId, Integer movementQuantity,
                       Integer previousQuantity, Integer newQuantity, MovementType type, String orderId) {
	    this.id = IdGenerator.generateTimeBasedUUID();
		this.occurredAt = OffsetDateTime.now(Clock.systemDefaultZone());

		this.productId = productId;
		this.movementQuantity = movementQuantity;
		this.previousQuantity = previousQuantity;
		this.newQuantity = newQuantity;
		this.type = type;
		this.orderId = orderId;

		super.registerEvent(StockMovementRegisteredEvent.builder()
				.movementId(this.id)
				.productId(this.productId)
				.type(this.type)
				.orderId(this.orderId)
				.movementQuantity(this.movementQuantity)
				.previousQuantity(this.previousQuantity)
				.newQuantity(this.newQuantity)
				.build());
    }

    public enum MovementType {
        STOCK_IN,
        STOCK_OUT
    }
}
