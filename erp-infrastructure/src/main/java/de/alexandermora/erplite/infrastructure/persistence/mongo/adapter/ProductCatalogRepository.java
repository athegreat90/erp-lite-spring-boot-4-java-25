package de.alexandermora.erplite.infrastructure.persistence.mongo.adapter;

import de.alexandermora.erplite.domain.port.repository.ProductCatalogRepositoryPort;
import de.alexandermora.erplite.domain.views.ProductView;
import de.alexandermora.erplite.infrastructure.persistence.mongo.mapper.ProductCatalogMapper;
import de.alexandermora.erplite.infrastructure.persistence.mongo.repository.ProductInCatalogRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static de.alexandermora.erplite.commons.constant.CacheConstants.*;

@Repository
@Slf4j
@AllArgsConstructor
public class ProductCatalogRepository implements ProductCatalogRepositoryPort {

    private final ProductInCatalogRepository productInCatalogRepository;
    private final ProductCatalogMapper productCatalogMapper;
    private final CacheManager cacheManager;

    @Override
    public Optional<ProductView> findById(String id) {
        log.info("Find product by id: {}", id);
        var cache = cacheManager.getCache(CACHE_PRODUCTS_BY_ID);
        if (cache != null) {
            var productInCache = cache.get(id, ProductView.class);
            if (productInCache != null) {
                log.info("Cache hit for product id: {}", id);
                return Optional.of(productInCache);
            }
        }
        return productInCatalogRepository.findById(id).map(productCatalogMapper::toView);
    }

    @Override
    public Optional<ProductView> findBySku(String sku) {
        log.info("Find product by SKU: {}", sku);
        var cache = cacheManager.getCache(CACHE_PRODUCTS_BY_SKU);
        if (cache != null) {
            var productInCache = cache.get(sku, ProductView.class);
            if (productInCache != null) {
                log.info("Cache hit for product SKU: {}", sku);
                return Optional.of(productInCache);
            }
        }
        return productInCatalogRepository.findBySku(sku).map(productCatalogMapper::toView);
    }

    @Override
    public List<ProductView> findByText(String text) {
        log.info("Find products by text: {}", text);
        return productInCatalogRepository.findByTextAndActive(text).stream().map(productCatalogMapper::toView).toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ProductView> findByCategory(String category) {
        log.info("Find products by category: {}", category);
        var cache = cacheManager.getCache(CACHE_PRODUCTS_BY_CATEGORY);
        if (cache != null) {
            var productsInCache = cache.get(category, List.class);
            if (productsInCache != null) {
                log.info("Cache hit for products by category: {}", category);
                return (List<ProductView>) productsInCache;
            }
        }
        return productInCatalogRepository.findByCategoryIdAndActiveTrue(category).stream().map(productCatalogMapper::toView).toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ProductView> findActive() {
        log.info("Find active products");
        var cache = cacheManager.getCache(CACHE_PRODUCTS_ACTIVE);
        if (cache != null) {
            var productsInCache = cache.get("active", List.class);
            if (productsInCache != null) {
                log.info("Cache hit for active products");
                return (List<ProductView>) productsInCache;
            }
        }
        return productInCatalogRepository.findByActiveTrueOrderByIdAsc().stream().map(productCatalogMapper::toView).toList();
    }
}
