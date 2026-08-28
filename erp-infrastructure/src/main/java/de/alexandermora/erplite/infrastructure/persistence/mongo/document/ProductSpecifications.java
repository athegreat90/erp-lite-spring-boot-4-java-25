package de.alexandermora.erplite.infrastructure.persistence.mongo.document;

/**
 * Technical specifications embedded in a {@link ProductInCatalogDocument}.
 */
public record ProductSpecifications(
        String processor,
        String ram,
        String storage,
        String display,
        String weight
) {
}