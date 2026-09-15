package de.alexandermora.erplite.application.query;

import de.alexandermora.erplite.application.exception.QueryException;
import de.alexandermora.erplite.commons.enums.CatalogType;
import de.alexandermora.erplite.domain.port.repository.CatalogRepositoryPort;
import de.alexandermora.erplite.domain.views.CatalogView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindCatalogByTypeQuery {
    private final CatalogRepositoryPort catalogRepository;

    public Optional<CatalogView> execute(CatalogType catalogType) {
        log.info("Executing FindCatalogByTypeQuery for catalogType: {}", catalogType);
        try {
            return catalogRepository.findByType(catalogType);
        } catch (RuntimeException e) {
            throw new QueryException(e.getMessage(), e);
        }
    }

}
