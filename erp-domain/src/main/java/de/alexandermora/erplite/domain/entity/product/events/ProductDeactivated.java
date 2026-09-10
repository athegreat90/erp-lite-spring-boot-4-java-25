package de.alexandermora.erplite.domain.entity.product.events;

import de.alexandermora.erplite.domain.common.DomainEvent;
import de.alexandermora.erplite.domain.entity.product.ProductId;

import java.time.Instant;

/**
 * Emitted when product is deactivated. TRIGGERS sync to MongoDB.
 */
public record ProductDeactivated(
        ProductId productId,
        Instant timestamp
) implements DomainEvent {
}