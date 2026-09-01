package de.alexandermora.erplite.domain.order.events;

import de.alexandermora.erplite.domain.common.DomainEvent;
import de.alexandermora.erplite.domain.order.OrderId;

import java.time.Instant;

/**
 * Emitted when order transitions PENDING -&gt; CONFIRMED. TRIGGERS stock decrement.
 */
public record OrderConfirmed(
        OrderId orderId,
        Instant timestamp
) implements DomainEvent {
}