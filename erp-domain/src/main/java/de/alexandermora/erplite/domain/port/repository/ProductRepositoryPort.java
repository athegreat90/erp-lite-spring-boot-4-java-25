package de.alexandermora.erplite.domain.port.repository;

import de.alexandermora.erplite.domain.entity.product.ProductRoot;
import de.alexandermora.erplite.domain.entity.product.ProductId;

import java.util.Optional;


/*
 * Port for storage or consult Products.
 * */
public interface ProductRepositoryPort {

    ProductRoot save(ProductRoot product);

    Optional<ProductRoot> findById(ProductId id);

    Optional<ProductRoot> findBySku(String sku);

    void delete(ProductRoot product);

}
