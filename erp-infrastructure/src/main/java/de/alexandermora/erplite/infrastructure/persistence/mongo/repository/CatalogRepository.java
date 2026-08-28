package de.alexandermora.erplite.infrastructure.persistence.mongo.repository;

import de.alexandermora.erplite.infrastructure.persistence.mongo.document.CatalogDocument;
import de.alexandermora.erplite.infrastructure.persistence.mongo.document.CatalogType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CatalogRepository extends MongoRepository<CatalogDocument, String> {

    Optional<CatalogDocument> findByCatalogType(CatalogType catalogType);

    List<CatalogDocument> findByActiveTrue();

    List<CatalogDocument> findByItemsCode(String code);
}