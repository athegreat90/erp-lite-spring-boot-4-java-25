package de.alexandermora.erplite.domain.port.repository;

import de.alexandermora.erplite.domain.entity.catalog.CatalogRoot;
import de.alexandermora.erplite.domain.entity.catalog.CatalogItem;
import de.alexandermora.erplite.domain.entity.catalog.CatalogType;

import java.util.List;
import java.util.Optional;

/*
 * Port read-only for Catalog
 * */
public interface CatalogRepositoryPort {
    Optional<CatalogRoot> findByType(CatalogType catalogType);
    List<CatalogItem> findItemsByType(CatalogType catalogType);
    Optional<CatalogItem> findItemByTypeAndCode(CatalogType catalogType, String code);
}
