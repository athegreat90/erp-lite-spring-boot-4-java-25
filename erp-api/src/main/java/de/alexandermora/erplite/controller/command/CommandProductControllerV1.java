package de.alexandermora.erplite.controller.command;

import de.alexandermora.erplite.application.command.product.CreateProductCommand;
import de.alexandermora.erplite.application.command.product.DeactivateProductCommand;
import de.alexandermora.erplite.application.command.product.UpdateProductCommand;
import de.alexandermora.erplite.application.command.product.UpdateStockCommand;
import de.alexandermora.erplite.application.usecase.product.CreateProductUseCase;
import de.alexandermora.erplite.application.usecase.product.DeactivateProductUseCase;
import de.alexandermora.erplite.application.usecase.product.UpdateProductUseCase;
import de.alexandermora.erplite.application.usecase.product.UpdateStockUseCase;
import de.alexandermora.erplite.paths.ApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(path = ApiPaths.COMMANDS_PRODUCTS, version = "1")
@RequiredArgsConstructor
@Tag(name = "Command Products", description = "Command endpoints for product management")
public class CommandProductControllerV1 {

    private final CreateProductUseCase createProductUseCase;
    private final DeactivateProductUseCase deactivateProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final UpdateStockUseCase updateStockUseCase;

    @Operation(summary = "Create product", description = "Creates a new product with its information and associated image")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product data"),
            @ApiResponse(responseCode = "409", description = "Business rule violation"),
            @ApiResponse(responseCode = "422", description = "Error processing the command"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void postProduct(
            @Parameter(description = "Product data to create", required = true)
            @Valid @RequestPart(value = "product") CreateProductCommand productCommandReq,
            @Parameter(description = "Product image", required = true)
            @RequestPart(value = "image") MultipartFile img,
            HttpServletResponse response) throws IOException {

        log.info("POST product");

        var command = new CreateProductCommand(
                productCommandReq.sku(),
                productCommandReq.name(),
                productCommandReq.description(),
                productCommandReq.price(),
                productCommandReq.currency(),
                productCommandReq.stock(),
                productCommandReq.categoryId(),
                img != null ? img.getBytes() : null,
                img != null ? img.getOriginalFilename() : null,
                productCommandReq.createdBy()
        );

        String productId = this.createProductUseCase.execute(command);

        response.setHeader("Location", ApiPaths.COMMANDS_PRODUCTS + "/" + productId);
    }

    @Operation(summary = "Update product", description = "Updates the information and image of an existing product")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product data"),
            @ApiResponse(responseCode = "409", description = "Business rule violation"),
            @ApiResponse(responseCode = "422", description = "Error processing the command"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putProduct(
            @Parameter(description = "Unique product identifier", required = true, example = "abc123")
            @PathVariable String id,
            @Parameter(description = "New product data", required = true)
            @Valid @RequestPart(value = "product") UpdateProductCommand productCommandReq,
            @Parameter(description = "New product image", required = true)
            @RequestPart(value = "image") MultipartFile img) throws IOException {

        log.info("PUT product");

        var command = new UpdateProductCommand(
                id,
                productCommandReq.name(),
                productCommandReq.description(),
                productCommandReq.price(),
                productCommandReq.categoryId(),
                img != null ? img.getBytes() : null,
                img != null ? img.getOriginalFilename() : null
        );

        this.updateProductUseCase.execute(command);
    }

    @Operation(summary = "Deactivate product", description = "Marks a product as inactive given its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Product deactivated successfully"),
            @ApiResponse(responseCode = "409", description = "Business rule violation"),
            @ApiResponse(responseCode = "422", description = "Error processing the command"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping(path = "/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patchProductDeactivate(
            @Parameter(description = "Unique product identifier", required = true, example = "abc123")
            @PathVariable String id) {

        log.info("PATCH product deactivate");

        var command = new DeactivateProductCommand(id);

        this.deactivateProductUseCase.execute(command);
    }

    @Operation(summary = "Update product stock", description = "Adjusts a product's inventory quantity, indicating the operation direction and reason")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Stock updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid stock data"),
            @ApiResponse(responseCode = "409", description = "Business rule violation"),
            @ApiResponse(responseCode = "422", description = "Error processing the command"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping(path = "/{id}/stock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patchProductStock(
            @Parameter(description = "Unique product identifier", required = true, example = "abc123")
            @PathVariable String id,
            @Parameter(description = "Command with the quantity, operation and reason for the stock adjustment", required = true)
            @Valid @RequestBody UpdateStockCommand stockCommand) {

        log.info("PATCH product stock");

        var command = new UpdateStockCommand(
                id,
                stockCommand.operation(),
                stockCommand.quantity(),
                stockCommand.reason()
        );

        this.updateStockUseCase.execute(command);
    }
}
