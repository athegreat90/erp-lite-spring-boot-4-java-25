package de.alexandermora.erplite.application.query;

import de.alexandermora.erplite.application.exception.QueryException;
import de.alexandermora.erplite.domain.port.repository.ProductCatalogRepositoryPort;
import de.alexandermora.erplite.domain.views.ProductView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindProductByCategoryQuery {
    private final ProductCatalogRepositoryPort productCatalogRepository;

    public List<ProductView> execute(String category) {
        log.info("Executing FindProductByCategoryQuery for category: {}", category);
        try {
            return productCatalogRepository.findByCategory(category);
        } catch (RuntimeException e) {
            throw new QueryException(e.getMessage(), e);
        }
    }
}
