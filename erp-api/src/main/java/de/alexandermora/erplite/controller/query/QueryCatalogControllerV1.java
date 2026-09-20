package de.alexandermora.erplite.controller.query;

import de.alexandermora.erplite.application.exception.QueryException;
import de.alexandermora.erplite.application.query.FindCatalogByTypeQuery;
import de.alexandermora.erplite.application.query.FindCatalogItemByCodeQuery;
import de.alexandermora.erplite.application.query.FindCatalogItemsByTypeQuery;
import de.alexandermora.erplite.commons.enums.CatalogType;
import de.alexandermora.erplite.domain.views.CatalogView;
import de.alexandermora.erplite.domain.views.ItemsView;
import de.alexandermora.erplite.dto.BaseResponseWrapper;
import de.alexandermora.erplite.paths.ApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = ApiPaths.QUERIES_CATALOGS, version = "1")
@RequiredArgsConstructor
@Tag(name = "Query Catalogs", description = "Query endpoints for catalogs and items")
public class QueryCatalogControllerV1 {

    private final FindCatalogByTypeQuery findCatalogByTypeQuery;
    private final FindCatalogItemsByTypeQuery findCatalogItemsByTypeQuery;
    private final FindCatalogItemByCodeQuery findCatalogItemByCodeQuery;

    @Operation(summary = "Get catalog by type", description = "Returns the catalog corresponding to the given type")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Catalog found"),
            @ApiResponse(responseCode = "404", description = "Catalog not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(path = "/{type}")
    public BaseResponseWrapper<CatalogView> getByType(
            @Parameter(description = "Catalog type (e.g. PRODUCT_CATEGORIES, CURRENCIES)", required = true, example = "PRODUCT_CATEGORIES")
            @PathVariable String type) {

        log.info("GET catalog by type: {}", type);

        CatalogType catalogType = CatalogType.valueOf(type.toUpperCase());

        CatalogView response = this.findCatalogByTypeQuery.execute(catalogType)
                .orElseThrow(() -> new QueryException("Catalog with type " + type + " not found"));

        return BaseResponseWrapper.of(response);
    }

    @Operation(summary = "Get catalog items by type", description = "Returns all items of the catalog for the specified type")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of catalog items")
    })
    @GetMapping(path = "/{type}/items")
    public BaseResponseWrapper<List<ItemsView>> getItemsByType(
            @Parameter(description = "Catalog type (e.g. PRODUCT_CATEGORIES, CURRENCIES)", required = true, example = "PRODUCT_CATEGORIES")
            @PathVariable String type) {

        log.info("GET catalog items by type: {}", type);

        CatalogType catalogType = CatalogType.valueOf(type.toUpperCase());

        List<ItemsView> response = this.findCatalogItemsByTypeQuery.execute(catalogType);

        return BaseResponseWrapper.of(response);
    }

    @Operation(summary = "Get catalog item by type and code", description = "Returns a specific catalog item filtering by type and code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item found"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @GetMapping(path = "/{type}/items", params = "code")
    public BaseResponseWrapper<ItemsView> getItemByTypeAndCode(
            @Parameter(description = "Catalog type (e.g. PRODUCT_CATEGORIES, CURRENCIES)", required = true, example = "PRODUCT_CATEGORIES")
            @PathVariable String type,
            @Parameter(description = "Item code within the catalog", required = true, example = "ELECTRONICS")
            @RequestParam String code) {

        log.info("GET catalog item by type: {} and code: {}", type, code);

        CatalogType catalogType = CatalogType.valueOf(type.toUpperCase());

        ItemsView response = this.findCatalogItemByCodeQuery.execute(catalogType, code)
                .orElseThrow(() -> new QueryException("Item with code " + code + " not found for type " + type));

        return BaseResponseWrapper.of(response);
    }
}
