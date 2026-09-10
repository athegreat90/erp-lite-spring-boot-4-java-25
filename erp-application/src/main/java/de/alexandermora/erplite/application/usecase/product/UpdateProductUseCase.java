package de.alexandermora.erplite.application.usecase.product;

import de.alexandermora.erplite.application.command.product.UpdateProductCommand;
import de.alexandermora.erplite.application.exception.CommandException;
import de.alexandermora.erplite.domain.entity.product.*;
import de.alexandermora.erplite.domain.port.repository.ProductRepositoryPort;
import de.alexandermora.erplite.domain.port.service.ImageStorageServicePort;
import de.alexandermora.erplite.domain.shared.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UpdateProductUseCase {
    private final ProductRepositoryPort productRepository;
    private final ImageStorageServicePort imageStorageService;

    public void execute(UpdateProductCommand command) {
        log.info("Updating product: {}", command.productId());

        try {
            // 1. Find product
            var product = findProductById(command.productId());

            log.debug("Current product: SKU={}, Name={}",
                    product.getSku().value(),
                    product.getName().value());

            // 2. Handle image update (if provided)
            var oldImage = product.getImage();
            var newImage = updateImage(command, oldImage);

            // 3. Build updated values
            var name = command.shouldUpdateName()
                    ? ProductName.of(command.name())
                    : product.getName();

            var description = command.description() != null
                    ? command.description()
                    : product.getDescription();

            var price = command.shouldUpdatePrice()
                    ? Money.of(command.price(), Currency.getInstance(product.getPrice().currency().getCurrencyCode()))
                    : product.getPrice();

            var category = command.shouldUpdateCategory()
                    ? CategoryReference.of(command.categoryId())
                    : product.getCategory();

            var finalImage = newImage != null ? newImage : product.getImage();

            // 4. Update product
            product.update(name, description, price, category, finalImage);

            log.debug("Product updated in domain");

            // 5. Persist changes
            productRepository.save(product);

            log.info("Product update persisted");

        } catch (IllegalArgumentException iae) {
            log.error("Invalid data for product update", iae);
            throw new CommandException("Error updating product: " + iae.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error updating product", e);
            throw new CommandException("Failed to update product: " + e.getMessage());
        }
    }

    private ProductRoot findProductById(String productId) {
        log.debug("Finding product by ID: {}", productId);

        ProductId productIdVO = ProductId.of(UUID.fromString(productId));

        return productRepository.findById(productIdVO)
                .orElseThrow(() -> {
                    log.warn("Product not found: {}", productId);
                    return new CommandException("Product not found with ID: " + productId);
                });
    }

    private ProductImage updateImage(UpdateProductCommand command, ProductImage oldImage) {
        if (!command.hasImage()) {
            log.debug("No image update requested");
            return null;
        }

        log.debug("Uploading new image: {}", command.imageName());

        try {
            // Upload new image
            var newImage = imageStorageService.upload(
                    command.imageName(),
                    command.imageData()
            );

            log.info("New image uploaded: {}", newImage.imageUrl());

            // Delete old image (if exists)
            if (oldImage != null) {

                imageStorageService.delete(oldImage);
                log.debug("Old image deleted: {}", oldImage.imageUrl());
            }

            return newImage;

        } catch (Exception e) {
            log.error("Failed to upload new image", e);
            throw new CommandException("Failed to upload product image: " + e.getMessage());
        }
    }

}
