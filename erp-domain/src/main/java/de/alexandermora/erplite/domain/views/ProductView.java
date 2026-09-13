package de.alexandermora.erplite.domain.views;

import java.util.List;
import java.util.Map;

public record ProductView(
        String sku,
        String name,
        String description,
        Double price,
        String money,
        Integer stock,
        String imageUrl,
        List<String> tags,
        Map<String, Object> specifications
) {
}
