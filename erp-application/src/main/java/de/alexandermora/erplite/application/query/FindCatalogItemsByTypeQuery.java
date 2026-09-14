package de.alexandermora.erplite.application.query;

import de.alexandermora.erplite.commons.enums.CatalogType;
import de.alexandermora.erplite.domain.port.repository.CatalogRepositoryPort;
import de.alexandermora.erplite.domain.views.ItemsView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindCatalogItemsByTypeQuery {
    private final CatalogRepositoryPort catalogRepository;

    public List<ItemsView> execute(CatalogType catalogType) {
        log.info("Executing FindCatalogItemsByTypeQuery for catalogType: {}", catalogType);
        return catalogRepository.findItemsByType(catalogType);
    }
}
