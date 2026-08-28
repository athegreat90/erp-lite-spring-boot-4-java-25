package de.alexandermora.erplite.infrastructure.persistence.mongo.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * Maps the {@code catalogs} collection in {@code erp_catalog_db} (reference data:
 * product categories, order statuses, payment / shipping methods, countries, currencies).
 * Indexes are owned by {@code db/mongodb/init/init-mongo.js}.
 */
@Document(collection = "catalogs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CatalogDocument {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private CatalogType catalogType;

    private String name;

    private String description;

    private boolean active;

    private List<CatalogItem> items;

    private Instant createdAt;

    private Instant updatedAt;
}