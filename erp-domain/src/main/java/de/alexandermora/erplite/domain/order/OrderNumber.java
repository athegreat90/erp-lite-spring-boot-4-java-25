package de.alexandermora.erplite.domain.order;

import java.time.Year;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * Unique order number. Pattern: ORD-2025-001.
 */
public record OrderNumber(String value) {

    private static final Pattern PATTERN = Pattern.compile("^ORD-\\d{4}-\\d{3}$");

    public OrderNumber {
        if (value == null || !PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("OrderNumber must match pattern ORD-YYYY-NNN: " + value);
        }
    }

    /**
     * Creates an OrderNumber from a String value.
     *
     * @param value the order number value
     * @return a new OrderNumber instance
     */
    public static OrderNumber of(String value) {
        return new OrderNumber(value);
    }

    /**
     * Generates a new OrderNumber with the current year and a sequence number.
     * Note: In a real implementation, the sequence should be retrieved from a database sequence or counter.
     *
     * @param sequence the sequence number (001-999)
     * @return a new OrderNumber instance
     */
    public static OrderNumber generate(int sequence) {
        if (sequence < 1 || sequence > 999) {
            throw new IllegalArgumentException("Sequence must be between 1 and 999");
        }
        int currentYear = Year.now().getValue();
        String orderNumber = String.format("ORD-%d-%03d", currentYear, sequence);
        return new OrderNumber(orderNumber);
    }
}