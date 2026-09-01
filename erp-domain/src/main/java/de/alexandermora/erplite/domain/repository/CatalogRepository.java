package de.alexandermora.erplite.domain.repository;

import de.alexandermora.erplite.domain.catalog.Catalog;
import de.alexandermora.erplite.domain.catalog.CatalogItem;
import de.alexandermora.erplite.domain.catalog.CatalogType;

import java.util.List;
import java.util.Optional;

/*
 * Port read-only for Catalog
 * */
public interface CatalogRepository {
    Optional<Catalog> findByType(CatalogType catalogType);
    List<CatalogItem> findItemsByType(CatalogType catalogType);
    Optional<CatalogItem> findItemByTypeAndCode(CatalogType catalogType, String code);
}
