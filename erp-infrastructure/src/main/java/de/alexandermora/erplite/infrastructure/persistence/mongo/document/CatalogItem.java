package de.alexandermora.erplite.infrastructure.persistence.mongo.document;

/**
 * One entry inside a {@link CatalogDocument#getItems() catalog}.
 * The populated fields of {@code metadata} depend on the parent catalog's
 * {@code catalogType}.
 */
public record CatalogItem(
        String id,
        String code,
        String value,
        String description,
        int displayOrder,
        CatalogItemMetadata metadata
) {
}