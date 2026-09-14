package de.alexandermora.erplite.commons.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheConstants {
    public static final String CACHE_PRODUCTS_BY_ID = "products:byId";
    public static final String CACHE_PRODUCTS_BY_SKU = "products:bySku";
    public static final String CACHE_PRODUCTS_BY_CATEGORY = "products:byCategory";
    public static final String CACHE_PRODUCTS_ACTIVE = "products:active";


    public static final String CACHE_CATALOGS_BY_TYPE = "catalogs:byType";
    public static final String CACHE_CATALOGS_ITEMS = "catalogs:items";


}
