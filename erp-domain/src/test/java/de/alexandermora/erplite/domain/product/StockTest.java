package de.alexandermora.erplite.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Stock Domain Test")
class StockTest {

    @Test
    @DisplayName("Should throw NullPointerException when value is null")
    void shouldThrowNullPointerExceptionWhenValueIsNull() {
        assertThrows(NullPointerException.class, () -> new Stock(null));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when value is negative")
    void shouldThrowIllegalArgumentExceptionWhenValueIsNegative() {
        var exception = assertThrows(IllegalArgumentException.class, () -> new Stock(-1));
        assertEquals("Stock cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Should accept zero as a valid value")
    void shouldAcceptZeroAsValidValue() {
        var stock = Stock.zero();
        assertEquals(0, stock.value());
    }

    @Test
    @DisplayName("Should create Stock via of factory")
    void shouldCreateStockViaOfFactory() {
        var stock = Stock.of(10);
        assertEquals(10, stock.value());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when incrementing by a negative quantity")
    void shouldThrowIllegalArgumentExceptionWhenIncrementingByNegativeQuantity() {
        var stock = Stock.of(5);
        var exception = assertThrows(IllegalArgumentException.class, () -> stock.increment(-1));
        assertEquals("quantity must be >= 0", exception.getMessage());
    }

    @Test
    @DisplayName("Should return an equal-value new instance when incrementing by zero")
    void shouldReturnEqualValueInstanceWhenIncrementingByZero() {
        var stock = Stock.of(5);
        var result = stock.increment(0);
        assertEquals(5, result.value());
    }

    @Test
    @DisplayName("Should increase stock when incrementing by a positive quantity")
    void shouldIncreaseStockWhenIncrementingByPositiveQuantity() {
        var stock = Stock.of(5);
        var result = stock.increment(3);
        assertEquals(8, result.value());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when decrementing by a negative quantity")
    void shouldThrowIllegalArgumentExceptionWhenDecrementingByNegativeQuantity() {
        var stock = Stock.of(5);
        var exception = assertThrows(IllegalArgumentException.class, () -> stock.decrement(-1));
        assertEquals("quantity must be >= 0", exception.getMessage());
    }

    @Test
    @DisplayName("Should return an equal-value new instance when decrementing by zero")
    void shouldReturnEqualValueInstanceWhenDecrementingByZero() {
        var stock = Stock.of(5);
        var result = stock.decrement(0);
        assertEquals(5, result.value());
    }

    @Test
    @DisplayName("Should allow decrementing down to exactly zero")
    void shouldAllowDecrementingDownToExactlyZero() {
        var stock = Stock.of(5);
        var result = stock.decrement(5);
        assertEquals(0, result.value());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when decrementing beyond available stock")
    void shouldThrowIllegalArgumentExceptionWhenDecrementingBeyondAvailableStock() {
        var stock = Stock.of(5);
        var exception = assertThrows(IllegalArgumentException.class, () -> stock.decrement(6));
        assertEquals("Insufficient stock", exception.getMessage());
    }

    @Test
    @DisplayName("Should decrease stock when decrementing by a valid quantity")
    void shouldDecreaseStockWhenDecrementingByValidQuantity() {
        var stock = Stock.of(5);
        var result = stock.decrement(2);
        assertEquals(3, result.value());
    }

    @Test
    @DisplayName("Should report available when value is greater than required")
    void shouldReportAvailableWhenValueGreaterThanRequired() {
        assertTrue(Stock.of(5).hasAvailable(3));
    }

    @Test
    @DisplayName("Should report available when value equals required (boundary)")
    void shouldReportAvailableWhenValueEqualsRequired() {
        assertTrue(Stock.of(5).hasAvailable(5));
    }

    @Test
    @DisplayName("Should report unavailable when value is less than required")
    void shouldReportUnavailableWhenValueLessThanRequired() {
        assertFalse(Stock.of(5).hasAvailable(6));
    }
}