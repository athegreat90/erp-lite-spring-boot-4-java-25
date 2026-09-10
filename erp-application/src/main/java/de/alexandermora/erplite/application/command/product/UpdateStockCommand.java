package de.alexandermora.erplite.application.command.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Command to adjust a Product's stock. Maps to {@code Product.incrementStock(int, String)}
 * or {@code Product.decrementStock(int, String)} depending on {@link #operation()};
 * {@code quantity} is always the positive magnitude of the change.
 */
public record UpdateStockCommand(
        @NotBlank(message = "productId cannot be null or blank")
        String productId,

        @NotNull(message = "operation cannot be null")
        StockOperation operation,

        @NotNull(message = "quantity cannot be null")
        @Min(value = 1, message = "quantity must be at least 1")
        Integer quantity,

        @NotBlank(message = "reason cannot be null or blank")
        @Size(min = 3, max = 100, message = "Reason must be between 3 and 100 characters")
        String reason
) {

    /**
     * Custom validation: quantity cannot be zero
     */
    public UpdateStockCommand {
        if (quantity != null && quantity == 0) {
            throw new IllegalArgumentException("Quantity cannot be zero");
        }
    }

    /**
     * Check if this is an increment operation.
     */
    public boolean isIncrement() {
        return quantity != null && quantity > 0;
    }

    /**
     * Check if this is a decrement operation.
     */
    public boolean isDecrement() {
        return quantity != null && quantity < 0;
    }

    /**
     * Get absolute quantity value.
     */
    public int absoluteQuantity() {
        return quantity != null ? Math.abs(quantity) : 0;
    }

}