package de.alexandermora.erplite.domain.order;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Order state with valid transitions. DELIVERED and CANCELLED are final states.
 */
public record OrderStatus(String value) {

    public static final String PENDING = "PENDING";
    public static final String CONFIRMED = "CONFIRMED";
    public static final String SHIPPED = "SHIPPED";
    public static final String DELIVERED = "DELIVERED";
    public static final String CANCELLED = "CANCELLED";

    private static final Set<String> VALID_STATUSES = Set.of(PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED);

    private static final Map<String, Set<String>> TRANSITIONS = Map.of(
            PENDING, Set.of(CONFIRMED, CANCELLED),
            CONFIRMED, Set.of(SHIPPED, CANCELLED),
            SHIPPED, Set.of(DELIVERED),
            DELIVERED, Set.of(),
            CANCELLED, Set.of()
    );

    public OrderStatus {
        Objects.requireNonNull(value, "value must not be null");
        if (!VALID_STATUSES.contains(value)) {
            throw new IllegalArgumentException("Invalid order status: " + value);
        }
    }

    public static OrderStatus of(String value) {
        return new OrderStatus(value);
    }

    public static OrderStatus pending() {
        return new OrderStatus(PENDING);
    }

    public static OrderStatus confirmed() {
        return new OrderStatus(CONFIRMED);
    }

    public static OrderStatus shipped() {
        return new OrderStatus(SHIPPED);
    }

    public static OrderStatus delivered() {
        return new OrderStatus(DELIVERED);
    }

    public static OrderStatus cancelled() {
        return new OrderStatus(CANCELLED);
    }

    public boolean canTransitionTo(OrderStatus nextStatus) {
        Objects.requireNonNull(nextStatus, "nextStatus must not be null");
        return TRANSITIONS.getOrDefault(this.value, Set.of()).contains(nextStatus.value);
    }

    public boolean isPending() {
        return PENDING.equals(value);
    }

    public boolean isConfirmed() {
        return CONFIRMED.equals(value);
    }

    public boolean isShipped() {
        return SHIPPED.equals(value);
    }

    public boolean isDelivered() {
        return DELIVERED.equals(value);
    }

    public boolean isCancelled() {
        return CANCELLED.equals(value);
    }

    public boolean isFinalState() {
        return isDelivered() || isCancelled();
    }
}