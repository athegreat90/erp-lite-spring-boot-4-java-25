package de.alexandermora.erplite.domain.product.events;

import de.alexandermora.erplite.domain.common.DomainEvent;
import de.alexandermora.erplite.domain.product.ProductId;

import java.time.Instant;

/**
 * Emitted when stock changes (increment or decrement). TRIGGERS sync to MongoDB.
 */
public record StockChanged(
        ProductId productId,
        Integer oldStock,
        Integer newStock,
        String reason,
        Instant timestamp
) implements DomainEvent {
}