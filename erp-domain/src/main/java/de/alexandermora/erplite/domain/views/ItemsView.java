package de.alexandermora.erplite.domain.views;

public record ItemsView(
        String code,
        String value,
        String description,
        Integer displayOrder
) {
}
