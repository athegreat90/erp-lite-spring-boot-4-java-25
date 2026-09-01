package de.alexandermora.erplite.domain.product.events;

import de.alexandermora.erplite.domain.common.DomainEvent;
import de.alexandermora.erplite.domain.product.ProductId;

import java.time.Instant;

/**
 * Emitted when product info is updated. TRIGGERS sync to MongoDB.
 */
public record ProductUpdated(
        ProductId productId,
        Instant timestamp
) implements DomainEvent {
}