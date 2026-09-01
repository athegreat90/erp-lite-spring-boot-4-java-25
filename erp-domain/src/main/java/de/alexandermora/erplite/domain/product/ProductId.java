package de.alexandermora.erplite.domain.product;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for Product aggregate.
 */
public record ProductId(UUID value) {

    public ProductId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static ProductId of(UUID value) {
        return new ProductId(value);
    }

    public static ProductId generate() {
        return new ProductId(UUID.randomUUID());
    }
}