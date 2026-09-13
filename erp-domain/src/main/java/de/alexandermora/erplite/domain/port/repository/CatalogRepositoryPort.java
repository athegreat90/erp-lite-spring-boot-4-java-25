package de.alexandermora.erplite.domain.port.repository;

import de.alexandermora.erplite.domain.views.CatalogType;
import de.alexandermora.erplite.domain.views.CatalogView;
import de.alexandermora.erplite.domain.views.ItemsView;

import java.util.List;
import java.util.Optional;

/*
 * Port read-only for Catalog
 * */
public interface CatalogRepositoryPort {
    Optional<CatalogView> findByType(CatalogType catalogType);

    List<ItemsView> findItemsByType(CatalogType catalogType);

    Optional<ItemsView> findItemByTypeAndCode(CatalogType catalogType, String code);
}
