package de.alexandermora.erplite.domain.product.events;

import de.alexandermora.erplite.domain.common.DomainEvent;
import de.alexandermora.erplite.domain.product.ProductId;
import de.alexandermora.erplite.domain.product.ProductName;
import de.alexandermora.erplite.domain.product.SKU;
import de.alexandermora.erplite.domain.shared.Money;

import java.time.Instant;

/**
 * Emitted when a new product is created. TRIGGERS sync to MongoDB (CQRS).
 */
public record ProductCreated(
        ProductId productId,
        SKU sku,
        ProductName name,
        Money price,
        Instant timestamp
) implements DomainEvent {
}