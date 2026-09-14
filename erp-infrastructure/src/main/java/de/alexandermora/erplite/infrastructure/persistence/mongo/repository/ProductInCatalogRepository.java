package de.alexandermora.erplite.infrastructure.persistence.mongo.repository;

import de.alexandermora.erplite.infrastructure.persistence.mongo.document.ProductInCatalogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductInCatalogRepository extends MongoRepository<ProductInCatalogDocument, String> {

    Optional<ProductInCatalogDocument> findBySku(String sku);

    List<ProductInCatalogDocument> findByNameContainingIgnoreCase(String name);

    @Query("{'$text': {'$search': ?0}, 'active': true}")
    List<ProductInCatalogDocument> findByTextAndActive(String text);

    List<ProductInCatalogDocument> findByCategoryIdAndActiveTrue(String categoryId);

    List<ProductInCatalogDocument> findByActiveTrueOrderByIdAsc();
}