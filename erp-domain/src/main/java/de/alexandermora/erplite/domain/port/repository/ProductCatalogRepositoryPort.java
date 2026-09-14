package de.alexandermora.erplite.domain.port.repository;

import de.alexandermora.erplite.domain.views.ProductView;

import java.util.List;
import java.util.Optional;

public interface ProductCatalogRepositoryPort {

    Optional<ProductView> findById(String id);

    Optional<ProductView> findBySku(String sku);

    List<ProductView> findByText(String text);

    List<ProductView> findByCategory(String category);

    List<ProductView> findActive();
}
