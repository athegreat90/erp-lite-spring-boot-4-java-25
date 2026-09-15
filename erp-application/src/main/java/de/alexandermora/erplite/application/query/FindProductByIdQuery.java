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
public class FindProductByIdQuery {
    private final ProductCatalogRepositoryPort productCatalogRepository;

    public Optional<ProductView> execute(String id) {
        log.info("Executing FindProductByIdQuery for id: {}", id);
        try {
            return productCatalogRepository.findById(id);
        } catch (RuntimeException e) {
            throw new QueryException(e.getMessage(), e);
        }
    }
}
