package de.alexandermora.erplite.application.command.product;

import jakarta.validation.constraints.NotBlank;

/**
 * Command to deactivate a Product. Targets {@code Product.deactivate()}.
 */
public record DeactivateProductCommand(
        @NotBlank(message = "productId cannot be null or blank")
        String productId
) {

}