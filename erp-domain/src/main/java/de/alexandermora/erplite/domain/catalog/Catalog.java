package de.alexandermora.erplite.domain.catalog;

import de.alexandermora.erplite.domain.common.AggregateRoot;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class Catalog extends AggregateRoot<String> {

    private final CatalogType catalogType;
    private final String name;
    private final String description;
    private final List<CatalogItem> items;
    private final boolean isActive;

    public Catalog(String id, CatalogType catalogType, String name, String description, List<CatalogItem> items, boolean isActive) {

        if (catalogType == null) {
            throw new IllegalArgumentException("Catalog type cannot be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Catalog name cannot be null or empty");
        }

        super(id);
        this.catalogType = catalogType;
        this.name = name;
        this.description = description;
        this.items = List.copyOf(items);
        this.isActive = isActive;
    }

    public Optional<CatalogItem> findItemByCode(String code) {
        return items.stream()
                .filter(item -> item.getCode().equals(code))
                .findFirst();
    }

    public boolean containsItem(String code) {
        return this.findItemByCode(code).isPresent();
    }

    public List<CatalogItem> findActiveItems() {
        return items.stream()
                .filter(CatalogItem::isActive)
                .toList();
    }

    public List<CatalogItem> findAll() {
        return items.stream()
                .toList();
    }
}
