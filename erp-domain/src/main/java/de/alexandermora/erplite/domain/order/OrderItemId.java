package de.alexandermora.erplite.domain.order;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for OrderItem entity.
 */
public record OrderItemId(UUID value) {

    public OrderItemId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static OrderItemId of(UUID value) {
        return new OrderItemId(value);
    }

    public static OrderItemId generate() {
        return new OrderItemId(UUID.randomUUID());
    }
}