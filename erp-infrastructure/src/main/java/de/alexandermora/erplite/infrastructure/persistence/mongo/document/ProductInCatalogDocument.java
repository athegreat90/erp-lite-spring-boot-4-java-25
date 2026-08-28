package de.alexandermora.erplite.infrastructure.persistence.mongo.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Maps the {@code product_documents} collection in {@code erp_catalog_db}: the
 * denormalized product catalog ({@code categoryName} is a snapshot alongside
 * {@code categoryId}, which references {@code catalogs.items[].id}).
 * Indexes are owned by {@code db/mongodb/init/init-mongo.js}.
 */
@Document(collection = "product_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductInCatalogDocument {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String sku;

    private String name;

    private String description;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal price;

    private String currency;

    private int stock;

    private String categoryId;

    private String categoryName;

    private String imageUrl;

    private boolean active;

    private List<String> tags;

    private ProductSpecifications specifications;

    private Instant createdAt;

    private Instant updatedAt;
}