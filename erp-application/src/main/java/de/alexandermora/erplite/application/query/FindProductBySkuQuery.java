package de.alexandermora.erplite.application.query;

import de.alexandermora.erplite.application.exception.QueryException;
import de.alexandermora.erplite.domain.port.repository.ProductCatalogRepositoryPort;
import de.alexandermora.erplite.domain.views.ProductView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindProductBySkuQuery {
    private final ProductCatalogRepositoryPort productCatalogRepository;

    public Optional<ProductView> execute(String sku) {
        log.info("Executing FindProductBySkuQuery for sku: {}", sku);
        try {
            return productCatalogRepository.findBySku(sku);
        } catch (RuntimeException e) {
            throw new QueryException(e.getMessage(), e);
        }
    }
}
