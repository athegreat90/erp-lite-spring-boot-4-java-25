package de.alexandermora.erplite.application.usecase.product;

import de.alexandermora.erplite.application.command.product.CreateProductCommand;
import de.alexandermora.erplite.application.command.product.UpdateProductCommand;
import de.alexandermora.erplite.application.exception.CommandException;
import de.alexandermora.erplite.domain.entity.product.*;
import de.alexandermora.erplite.domain.port.repository.ProductRepositoryPort;
import de.alexandermora.erplite.domain.port.service.ImageStorageServicePort;
import de.alexandermora.erplite.domain.port.service.OrderConfirmEmailServicePort;
import de.alexandermora.erplite.domain.shared.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CreateProductUseCase {
    private final ProductRepositoryPort productRepository;
    private final ImageStorageServicePort imageStorageService;


    public String execute(CreateProductCommand command) {
        log.info("Creating product with SKU: {}", command.sku());

        try {
            // 1. Validate SKU uniqueness
            validateSkuUniqueness(command.sku());

            // 2. Upload image (if provided)
            var image = updateImage(command);

            // 3. Create value objects
            var sku = SKU.of(command.sku());
            var name = ProductName.of(command.name());
            var price = Money.of(command.price(), Currency.getInstance(command.currency()));
            var stock = Stock.of(command.stock());
            var category = CategoryReference.of(command.categoryId());

            // 4. Create product aggregate
            var product = ProductRoot.create(
                    sku,
                    name,
                    command.description(),
                    price,
                    stock,
                    category,
                    image,
                    command.createdBy()
            );

            log.debug("Product created in domain with ID: {}", product.getId().value());

            // 5. Persist product
            var savedProduct = productRepository.save(product);

            log.info("Product persisted with ID: {}", savedProduct.getId().value());

            // TODO: Handle domain events - Sync to MongoDB

            return savedProduct.getId().value().toString();

        } catch (IllegalArgumentException iae) {
            log.error("Invalid data for product creation");
            throw new CommandException("Error creating product: " + iae.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error creating product", e);
            throw new CommandException("Failed to create product: " + e.getMessage());
        }
    }

    private void validateSkuUniqueness(String sku) {
        log.debug("Validating SKU uniqueness: {}", sku);

        if (productRepository.findBySku(sku).isPresent()) {
            log.warn("SKU already exists: {}", sku);
            throw new CommandException("Product with SKU '" + sku + "' already exists");
        }
    }


    private ProductImage updateImage(CreateProductCommand command) {
        if (!command.hasImage()) {
            log.debug("No image update requested");
            return null;
        }

        log.debug("Uploading new image: {} with SKU {}", command.imageName(), command.sku());

        try {
            // Upload new image
            var newImage = imageStorageService.upload(command.imageName(), command.imageData());

            log.info("New image uploaded: {}", newImage.imageUrl());
            return newImage;

        } catch (Exception e) {
            log.error("Failed to upload new image", e);
            throw new CommandException("Failed to upload product image: " + e.getMessage());
        }
    }
}
