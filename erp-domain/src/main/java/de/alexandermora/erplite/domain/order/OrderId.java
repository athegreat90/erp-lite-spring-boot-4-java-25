package de.alexandermora.erplite.domain.order;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for Order aggregate.
 */
public record OrderId(UUID value) {

    public OrderId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static OrderId of(UUID value) {
        return new OrderId(value);
    }

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }
}