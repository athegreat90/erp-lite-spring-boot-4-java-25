package de.alexandermora.erplite.infrastructure.persistence.mongo.document;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.util.List;

/**
 * Metadata embedded in a {@link CatalogItem}. Only the components relevant to the parent
 * catalog's {@code catalogType} are populated (see {@code db/mongodb/init/init-mongo.js});
 * the rest are {@code null}.
 */
public record CatalogItemMetadata(
        String icon,                // PRODUCT_CATEGORIES, PAYMENT_METHODS, SHIPPING_METHODS
        String color,               // PRODUCT_CATEGORIES, ORDER_STATUSES
        List<String> nextStatuses,  // ORDER_STATUSES (may be empty)
        @Field(targetType = FieldType.DECIMAL128) BigDecimal fee,   // PAYMENT_METHODS
        @Field(targetType = FieldType.DECIMAL128) BigDecimal cost,  // SHIPPING_METHODS
        Integer estimatedDays,      // SHIPPING_METHODS
        String flag,                // COUNTRIES
        String currency,            // COUNTRIES
        String phonePrefix,         // COUNTRIES
        String symbol,              // CURRENCIES
        Integer decimalPlaces       // CURRENCIES
) {
}