package de.alexandermora.erplite.domain.shared;

import java.util.Objects;

/**
 * Quantity of items in order. Must be greater than 0.
 */
public record Quantity(Integer value) {

    public Quantity {
        Objects.requireNonNull(value, "value must not be null");
        if (value <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }

    public static Quantity of(int value) {
        return new Quantity(value);
    }
}