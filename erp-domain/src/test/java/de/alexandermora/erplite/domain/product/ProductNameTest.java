package de.alexandermora.erplite.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductName Domain Test")
class ProductNameTest {

    @Test
    @DisplayName("Should throw IllegalArgumentException when value is null")
    void shouldThrowIllegalArgumentExceptionWhenValueIsNull() {
        var exception = assertThrows(IllegalArgumentException.class, () -> new ProductName(null));
        assertEquals("value must not be null", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "a", "ab"})
    @DisplayName("Should throw IllegalArgumentException when value is shorter than 3 characters")
    void shouldThrowIllegalArgumentExceptionWhenTooShort(String value) {
        var exception = assertThrows(IllegalArgumentException.class, () -> new ProductName(value));
        assertEquals("ProductName length must be between 3 and 200 characters", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when value is longer than 200 characters")
    void shouldThrowIllegalArgumentExceptionWhenTooLong() {
        var value = "a".repeat(201);
        var exception = assertThrows(IllegalArgumentException.class, () -> new ProductName(value));
        assertEquals("ProductName length must be between 3 and 200 characters", exception.getMessage());
    }

    @Test
    @DisplayName("Should accept the minimum boundary length of 3")
    void shouldAcceptMinimumBoundaryLength() {
        var name = new ProductName("abc");
        assertEquals("abc", name.value());
    }

    @Test
    @DisplayName("Should accept the maximum boundary length of 200")
    void shouldAcceptMaximumBoundaryLength() {
        var value = "a".repeat(200);
        var name = ProductName.of(value);
        assertEquals(value, name.value());
    }

    @Test
    @DisplayName("Should accept a normal mid-length value")
    void shouldAcceptNormalMidLengthValue() {
        var name = ProductName.of("Wireless Mouse");
        assertEquals("Wireless Mouse", name.value());
    }
}