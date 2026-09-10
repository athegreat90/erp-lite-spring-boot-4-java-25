package de.alexandermora.erplite.domain.entity.product.events;

import de.alexandermora.erplite.domain.common.DomainEvent;
import de.alexandermora.erplite.domain.entity.product.ProductId;
import de.alexandermora.erplite.domain.entity.product.ProductName;
import de.alexandermora.erplite.domain.entity.product.SKU;
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