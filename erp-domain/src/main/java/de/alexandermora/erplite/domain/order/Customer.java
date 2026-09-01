package de.alexandermora.erplite.domain.order;

import de.alexandermora.erplite.domain.shared.CustomerId;

import java.util.Objects;

/**
 * Customer reference with basic info.
 */
public record Customer(CustomerId customerId, String customerName) {

    public Customer {
        Objects.requireNonNull(customerId, "customerId must not be null");
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("customerName must not be blank");
        }
    }

    public static Customer of(CustomerId customerId, String customerName) {
        return new Customer(customerId, customerName);
    }
}