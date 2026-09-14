package de.alexandermora.erplite.application.query;

import de.alexandermora.erplite.domain.port.repository.ProductCatalogRepositoryPort;
import de.alexandermora.erplite.domain.views.ProductView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindProductByTextQuery {
    private final ProductCatalogRepositoryPort productCatalogRepository;

    public List<ProductView> execute(String text) {
        log.info("Executing FindProductByTextQuery for text: {}", text);
        return productCatalogRepository.findByText(text);
    }
}
