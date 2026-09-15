package de.alexandermora.erplite.controller.query;

import de.alexandermora.erplite.application.exception.QueryException;
import de.alexandermora.erplite.application.query.*;
import de.alexandermora.erplite.commons.enums.CatalogType;
import de.alexandermora.erplite.domain.views.CatalogView;
import de.alexandermora.erplite.domain.views.ItemsView;
import de.alexandermora.erplite.dto.BaseResponseWrapper;
import de.alexandermora.erplite.path.ApiPath;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(ApiPath.QUERIES_CATALOGS)
@RequiredArgsConstructor
public class CatalogQueryController {
    private final FindCatalogByTypeQuery findCatalogByTypeQuery;
    private final FindCatalogItemsByTypeQuery findCatalogItemsByTypeQuery;
    private final FindCatalogItemByCodeQuery findCatalogItemByCodeQuery;

    @GetMapping(path = "/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponseWrapper<CatalogView> getByType(@PathVariable String type) {
        log.info("Received request to get catalog by type: {}", type);
        var catalogType = resolveCatalogType(type);
        return findCatalogByTypeQuery.execute(catalogType)
                .map(BaseResponseWrapper::of)
                .orElseThrow(() -> new QueryException("Catalog not found for type: " + type));
    }

    @GetMapping(path = "/{type}/items", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponseWrapper<List<ItemsView>> getItemsByType(@PathVariable String type, HttpServletResponse response) {
        log.info("Received request to get catalog items by type: {}", type);
        var catalogType = resolveCatalogType(type);
        var itemsViewList = findCatalogItemsByTypeQuery.execute(catalogType);
        if (itemsViewList.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNPROCESSABLE_CONTENT);
            return BaseResponseWrapper.of(List.of());
        }
        return BaseResponseWrapper.of(itemsViewList);
    }

    @GetMapping(path = "/{type}/items", produces = MediaType.APPLICATION_JSON_VALUE, params = "code")
    public BaseResponseWrapper<ItemsView> getItemByCode(@PathVariable String type, @RequestParam String code) {
        log.info("Received request to get catalog item by type: {} and code: {}", type, code);
        var catalogType = resolveCatalogType(type);
        return findCatalogItemByCodeQuery.execute(catalogType, code)
                .map(BaseResponseWrapper::of)
                .orElseThrow(() -> new QueryException("Catalog item not found for type: " + type + " and code: " + code));
    }

    private CatalogType resolveCatalogType(String type) {
        try {
            return CatalogType.of(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new QueryException("Invalid catalog type: " + type, e);
        }
    }
}
