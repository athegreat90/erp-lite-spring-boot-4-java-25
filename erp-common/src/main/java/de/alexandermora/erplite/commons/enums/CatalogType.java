package de.alexandermora.erplite.commons.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * Types of catalogs available in the system.
 * These values match the catalogType field in MongoDB catalogs collection.
 */
@Getter
public enum CatalogType {

    PRODUCT_CATEGORIES,
    ORDER_STATUSES,
    PAYMENT_METHODS,
    SHIPPING_METHODS,
    COUNTRIES,
    CURRENCIES;

    public static CatalogType of(String value) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid catalog type: " + value));
    }
}