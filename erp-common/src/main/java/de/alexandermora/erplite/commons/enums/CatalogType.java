package de.alexandermora.erplite.commons.enums;

import lombok.Getter;

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
    CURRENCIES
}