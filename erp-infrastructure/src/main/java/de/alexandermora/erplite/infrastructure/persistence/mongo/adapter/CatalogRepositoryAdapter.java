package de.alexandermora.erplite.infrastructure.persistence.mongo.adapter;

import de.alexandermora.erplite.commons.enums.CatalogType;
import de.alexandermora.erplite.domain.port.repository.CatalogRepositoryPort;
import de.alexandermora.erplite.domain.views.CatalogView;
import de.alexandermora.erplite.domain.views.ItemsView;
import de.alexandermora.erplite.infrastructure.persistence.mongo.mapper.CatalogMapper;
import de.alexandermora.erplite.infrastructure.persistence.mongo.repository.CatalogRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static de.alexandermora.erplite.commons.constant.CacheConstants.*;

@Repository
@Slf4j
@AllArgsConstructor
public class CatalogRepositoryAdapter implements CatalogRepositoryPort {

    private final CatalogRepository catalogMongoRepository;
    private final CatalogMapper catalogMapper;
    private final CacheManager cacheManager;

    @Override
    public Optional<CatalogView> findByType(CatalogType catalogType) {
        var cache = cacheManager.getCache(CACHE_CATALOGS_BY_TYPE);
        if (cache != null) {
            var catalogInCache = cache.get(catalogType.name(), CatalogView.class);
            if (catalogInCache != null) {
                log.info("Cache hit for catalog type: {}", catalogType);
                return Optional.of(catalogInCache);
            }
        }
        return catalogMongoRepository.findByCatalogType(catalogType).map(catalogMapper::toView);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ItemsView> findItemsByType(CatalogType catalogType) {

        var cache = cacheManager.getCache(CACHE_CATALOGS_ITEMS);
        if (cache != null) {
            var itemsInCache = cache.get(catalogType.name(), List.class);
            if (itemsInCache != null) {
                log.info("Cache hit for catalog items of type: {}", catalogType);
                return (List<ItemsView>) itemsInCache;
            }
        }
        return catalogMongoRepository.findByCatalogType(catalogType)
                .map(catalogDocument -> catalogDocument.getItems().stream()
                        .map(catalogMapper::toItemView)
                        .toList())
                .orElse(List.of());
    }

    @Override
    public Optional<ItemsView> findItemByTypeAndCode(CatalogType catalogType, String code) {

        return catalogMongoRepository.findByCatalogType(catalogType)
                .flatMap(catalogDocument -> catalogDocument.getItems().stream()
                        .filter(item -> item.code().equals(code))
                        .findFirst()
                        .map(catalogMapper::toItemView));
    }
}
