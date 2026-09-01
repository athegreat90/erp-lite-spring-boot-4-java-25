package de.alexandermora.erplite.domain.product;

import java.util.Objects;

/**
 * Product stock quantity. Cannot be negative.
 */
public record Stock(Integer value) {

    public Stock {
        Objects.requireNonNull(value, "value must not be null");
        if (value < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
    }

    public static Stock of(int value) {
        return new Stock(value);
    }

    public static Stock zero() {
        return new Stock(0);
    }

    public Stock increment(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("quantity must be >= 0");
        }
        return new Stock(this.value + quantity);
    }

    public Stock decrement(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("quantity must be >= 0");
        }
        int result = this.value - quantity;
        if (result < 0) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        return new Stock(result);
    }

    public boolean hasAvailable(int required) {
        return this.value >= required;
    }
}