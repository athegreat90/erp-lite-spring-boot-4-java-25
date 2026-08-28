package de.alexandermora.erplite.infrastructure.persistence.mongo.document;

/**
 * Kinds of reference catalog held in the {@code catalogs} collection.
 * Mirrors the {@code catalogType} values seeded by {@code db/mongodb/init/init-mongo.js}.
 */
public enum CatalogType {
    PRODUCT_CATEGORIES,
    ORDER_STATUSES,
    PAYMENT_METHODS,
    SHIPPING_METHODS,
    COUNTRIES,
    CURRENCIES
}