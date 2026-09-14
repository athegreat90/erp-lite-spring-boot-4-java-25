package de.alexandermora.erplite.domain.views;

import de.alexandermora.erplite.commons.enums.CatalogType;

import java.time.Instant;
import java.util.List;

public record CatalogView(
        Boolean active,
        String name,
        String description,
        CatalogType type,
        Instant createdAt,
        Instant updatedAt,
        List<ItemsView> items
) {
}
