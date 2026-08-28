package de.alexandermora.erplite.infrastructure.persistence.mongo.repository;

import de.alexandermora.erplite.infrastructure.persistence.mongo.document.ProductInCatalogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductInCatalogRepository extends MongoRepository<ProductInCatalogDocument, String> {

    Optional<ProductInCatalogDocument> findBySku(String sku);

    boolean existsBySku(String sku);

    List<ProductInCatalogDocument> findByCategoryId(String categoryId);

    List<ProductInCatalogDocument> findByActiveTrue();

    List<ProductInCatalogDocument> findByTagsContaining(String tag);
}