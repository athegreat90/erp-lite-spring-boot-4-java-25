package de.alexandermora.erplite.application.query;

import de.alexandermora.erplite.application.exception.QueryException;
import de.alexandermora.erplite.commons.enums.CatalogType;
import de.alexandermora.erplite.domain.port.repository.CatalogRepositoryPort;
import de.alexandermora.erplite.domain.views.ItemsView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindCatalogItemByCodeQuery {
    private final CatalogRepositoryPort catalogRepository;

    public Optional<ItemsView> execute(CatalogType catalogType, String code) {
        log.info("Executing FindCatalogItemByCodeQuery for catalogType: {} and code: {}", catalogType, code);
        try {
            return catalogRepository.findItemByTypeAndCode(catalogType, code);
        } catch (RuntimeException e) {
            throw new QueryException(e.getMessage(), e);
        }
    }
}
