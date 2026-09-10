package de.alexandermora.erplite.infrastructure.persistence.jpa.adapter;

import de.alexandermora.erplite.domain.entity.product.ProductId;
import de.alexandermora.erplite.domain.entity.product.ProductRoot;
import de.alexandermora.erplite.domain.port.repository.ProductRepositoryPort;
import de.alexandermora.erplite.infrastructure.persistence.jpa.mapper.ProductJpaMapper;
import de.alexandermora.erplite.infrastructure.persistence.jpa.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
@AllArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductRepository productRepository;
    private final ProductJpaMapper productJpaMapper;

    @Override
    public ProductRoot save(ProductRoot product) {
        try {
            var entity = productJpaMapper.toEntity(product);
            entity.setId(product.getId().value());
            log.info("Saving product with sku: {}", entity.getSku());
            var productSaved = productRepository.save(entity);
            log.info("Saved product with id: {}", productSaved.getId());
            return productJpaMapper.toDomain(productSaved);
        } catch (Exception e) {
            log.error("Error saving product", e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Optional<ProductRoot> findById(ProductId id) {
        try {
            var entityOpt = productRepository.findById(id.value());
            if (entityOpt.isEmpty()) {
                log.debug("Product with id {} not found", id.value());
                return Optional.empty();
            }
            return entityOpt.map(productJpaMapper::toDomain);
        } catch (Exception e) {
            log.error("Error finding product by id", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ProductRoot> findBySku(String sku) {
        try {
            var entityOpt = productRepository.findBySku(sku);
            if (entityOpt.isEmpty()) {
                log.debug("Product with sku {} not found", sku);
                return Optional.empty();
            }
            return entityOpt.map(productJpaMapper::toDomain);
        } catch (Exception e) {
            log.error("Error finding product by sku", e);
            return Optional.empty();
        }
    }

    @Override
    public void delete(ProductRoot product) {
        try {
            var entity = productJpaMapper.toEntity(product);
            productRepository.delete(entity);
            log.info("Deleted product with id: {}", product.getId().value());
        } catch (Exception e) {
            log.error("Error deleting product", e);
            throw new IllegalStateException(e);
        }
    }
}
