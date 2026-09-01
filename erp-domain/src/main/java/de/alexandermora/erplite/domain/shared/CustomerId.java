package de.alexandermora.erplite.domain.shared;

import java.util.Objects;

/**
 * Reference to external customer system (JSONPlaceholder).
 */
public record CustomerId(Long value) {

    public CustomerId {
        Objects.requireNonNull(value, "value must not be null");
        if (value <= 0) {
            throw new IllegalArgumentException("value must be > 0");
        }
    }

    public static CustomerId of(Long value) {
        return new CustomerId(value);
    }
}