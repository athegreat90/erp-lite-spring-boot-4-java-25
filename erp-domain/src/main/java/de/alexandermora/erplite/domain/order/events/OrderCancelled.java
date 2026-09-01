package de.alexandermora.erplite.domain.order.events;

import de.alexandermora.erplite.domain.common.DomainEvent;
import de.alexandermora.erplite.domain.order.OrderId;

import java.time.Instant;

/**
 * Emitted when order is cancelled. If was CONFIRMED, stock must be released.
 */
public record OrderCancelled(
        OrderId orderId,
        String reason,
        Instant timestamp
) implements DomainEvent {
}