package de.alexandermora.erplite.controller.query;

import de.alexandermora.erplite.application.exception.QueryException;
import de.alexandermora.erplite.application.query.*;
import de.alexandermora.erplite.domain.views.ProductView;
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
@RequestMapping(ApiPath.QUERIES_PRODUCTS)
@RequiredArgsConstructor
public class ProductQueryController {
    private final FindProductByCategoryQuery findProductByCategory;
    private final FindProductByIdQuery findProductByIdQuery;
    private final FindProductActiveQuery findProductActiveQuery;
    private final FindProductByTextQuery findProductByTextQuery;
    private final FindProductBySkuQuery findProductBySkuQuery;


    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponseWrapper<ProductView> getById(@PathVariable String id) {
        log.info("Received request to get product by id: {}", id);
        return findProductByIdQuery.execute(id)
                .map(BaseResponseWrapper::of)
                .orElseThrow(() -> new QueryException("Product not found for id: " + id));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, params = "sku")
    public BaseResponseWrapper<ProductView> getBySku(@RequestParam String sku) {
        log.info("Received request to get product by sku: {}", sku);
        return findProductBySkuQuery.execute(sku)
                .map(BaseResponseWrapper::of)
                .orElseThrow(() -> new QueryException("Product not found for sku: " + sku));
    }

    @GetMapping(path = "/active", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponseWrapper<List<ProductView>> getActiveProducts(HttpServletResponse response) {
        log.info("Received request to get active products");
        var productViewList = findProductActiveQuery.execute();
        if (productViewList.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return BaseResponseWrapper.of(List.of());
        }
        return BaseResponseWrapper.of(productViewList);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, params = "category")
    public BaseResponseWrapper<List<ProductView>> getByCategory(@RequestParam String category, HttpServletResponse response) {
        log.info("Received request to get products by category: {}", category);
        var productViewList = findProductByCategory.execute(category);
        if (productViewList.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNPROCESSABLE_CONTENT);
            return BaseResponseWrapper.of(List.of());
        }
        return BaseResponseWrapper.of(productViewList);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, params = "text")
    public BaseResponseWrapper<List<ProductView>> getByText(@RequestParam String text, HttpServletResponse response) {
        log.info("Received request to get products by text: {}", text);
        var productViewList = findProductByTextQuery.execute(text);
        if (productViewList.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return BaseResponseWrapper.of(List.of());
        }
        return BaseResponseWrapper.of(productViewList);
    }
}
