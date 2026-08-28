package de.alexandermora.erplite.infrastructure.persistence.jpa.entity;

/**
 * Lifecycle states for an {@link OrderEntity}.
 * Mirrors the {@code chk_order_status} CHECK constraint on {@code orders.status}.
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}