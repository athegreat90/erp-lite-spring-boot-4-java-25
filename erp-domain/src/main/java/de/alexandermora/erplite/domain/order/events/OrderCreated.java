package de.alexandermora.erplite.domain.order.events;

import de.alexandermora.erplite.domain.common.DomainEvent;
import de.alexandermora.erplite.domain.order.OrderId;
import de.alexandermora.erplite.domain.shared.CustomerId;
import de.alexandermora.erplite.domain.shared.Money;

import java.time.Instant;

/**
 * Emitted when a new order is created.
 */
public record OrderCreated(
        OrderId orderId,
        CustomerId customerId,
        String customerName,
        Money totalAmount,
        Instant timestamp
) implements DomainEvent {
}