package de.alexandermora.erplite.controller.query;

import de.alexandermora.erplite.application.exception.QueryException;
import de.alexandermora.erplite.application.query.FindProductActiveQuery;
import de.alexandermora.erplite.application.query.FindProductByCategoryQuery;
import de.alexandermora.erplite.application.query.FindProductByIdQuery;
import de.alexandermora.erplite.application.query.FindProductBySkuQuery;
import de.alexandermora.erplite.application.query.FindProductByTextQuery;
import de.alexandermora.erplite.domain.views.ProductView;
import de.alexandermora.erplite.dto.BaseResponseWrapper;
import de.alexandermora.erplite.paths.ApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = ApiPaths.QUERIES_PRODUCTS, version = "1")
@RequiredArgsConstructor
@Tag(name = "Query Products", description = "Query endpoints for products")
public class QueryProductControllerV1 {

    private final FindProductByIdQuery findProductByIdQuery;
    private final FindProductBySkuQuery findProductBySkuQuery;
    private final FindProductActiveQuery findProductActiveQuery;
    private final FindProductByTextQuery findProductByTextQuery;
    private final FindProductByCategoryQuery findProductByCategoryQuery;

    @Operation(summary = "Get product by ID", description = "Returns a product given its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(path = "/{id}")
    public BaseResponseWrapper<ProductView> getById(
            @Parameter(description = "Unique product identifier", required = true, example = "abc123")
            @PathVariable String id) {

        log.info("GET product by id: {}", id);

        ProductView response = this.findProductByIdQuery.execute(id)
                .orElseThrow(() -> new QueryException("Product with id " + id + " not found"));

        return BaseResponseWrapper.of(response);
    }

    @Operation(summary = "Get product by SKU", description = "Returns a product given its SKU code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(params = "sku")
    public BaseResponseWrapper<ProductView> getBySku(
            @Parameter(description = "Product SKU code", required = true, example = "SKU-001")
            @RequestParam String sku) {

        log.info("GET product by sku: {}", sku);

        ProductView response = this.findProductBySkuQuery.execute(sku)
                .orElseThrow(() -> new QueryException("Product with sku " + sku + " not found"));

        return BaseResponseWrapper.of(response);
    }

    @Operation(summary = "Get active products", description = "Returns the list of all active products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of active products"),
            @ApiResponse(responseCode = "204", description = "No active products"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(path = "/active")
    public BaseResponseWrapper<List<ProductView>> getActive(HttpServletResponse response) {

        log.info("GET product active");

        List<ProductView> products = this.findProductActiveQuery.execute();

        if (products.isEmpty()) {
            log.info("No active products found");
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }

        return BaseResponseWrapper.of(products);
    }

    @Operation(summary = "Search products by text", description = "Returns products whose name or description matches the search text")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public BaseResponseWrapper<List<ProductView>> search(
            @Parameter(description = "Text to search in product name or description", required = true, example = "laptop")
            @RequestParam String text) {

        log.info("GET search: {}", text);

        List<ProductView> products = this.findProductByTextQuery.execute(text);

        return BaseResponseWrapper.of(products);
    }

    @Operation(summary = "Get products by category", description = "Returns all products belonging to the given category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of products by category"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(params = "category")
    public BaseResponseWrapper<List<ProductView>> findByCategory(
            @Parameter(description = "Category identifier to filter by", required = true, example = "cat-electronics")
            @RequestParam String category) {

        log.info("GET findByCategory: {}", category);

        List<ProductView> products = this.findProductByCategoryQuery.execute(category);

        return BaseResponseWrapper.of(products);
    }
}
