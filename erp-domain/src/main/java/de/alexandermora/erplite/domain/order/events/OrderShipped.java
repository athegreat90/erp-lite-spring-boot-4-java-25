package de.alexandermora.erplite.domain.order.events;

import de.alexandermora.erplite.domain.common.DomainEvent;
import de.alexandermora.erplite.domain.order.OrderId;

import java.time.Instant;

/**
 * Emitted when order transitions CONFIRMED -&gt; SHIPPED.
 */
public record OrderShipped(
        OrderId orderId,
        Instant timestamp
) implements DomainEvent {
}