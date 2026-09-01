package de.alexandermora.erplite.domain.product;

/**
 * Reference to Catalog in MongoDB. Example: cat-electronics.
 */
public record CategoryReference(String categoryId) {

    public CategoryReference {
        if (categoryId == null || categoryId.isBlank()) {
            throw new IllegalArgumentException("categoryId must not be blank");
        }
    }

    public static CategoryReference of(String categoryId) {
        return new CategoryReference(categoryId);
    }
}