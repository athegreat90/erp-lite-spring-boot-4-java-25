package de.alexandermora.erplite.domain.product;

import java.util.regex.Pattern;

/**
 * Stock Keeping Unit. Unique and immutable. Pattern: LAPTOP-001.
 */
public record SKU(String value) {

    private static final Pattern PATTERN = Pattern.compile("[A-Z]+-\\d{3}");

    public SKU {
        if (value == null || !PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("SKU must match pattern [A-Z]+-NNN: " + value);
        }
    }

    public static SKU of(String value) {
        return new SKU(value);
    }
}