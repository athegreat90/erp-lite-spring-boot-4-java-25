package de.alexandermora.erplite.domain.entity.order.events;

import de.alexandermora.erplite.domain.common.DomainEvent;
import de.alexandermora.erplite.domain.entity.order.OrderId;

import java.time.Instant;

/**
 * Emitted when order transitions SHIPPED -&gt; DELIVERED. Final state.
 */
public record OrderDelivered(
        OrderId orderId,
        Instant timestamp
) implements DomainEvent {
}