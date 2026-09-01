package de.alexandermora.erplite.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Quantity Domain Test")
class QuantityTest {

    @Test
    @DisplayName("Should throw NullPointerException when value is null")
    void shouldThrowWhenValueIsNull() {
        assertThrows(NullPointerException.class, () -> new Quantity(null));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("Should throw IllegalArgumentException when value is not greater than 0")
    void shouldThrowWhenValueNotPositive(int value) {
        var exception = assertThrows(IllegalArgumentException.class, () -> new Quantity(value));
        assertEquals("Quantity must be greater than 0", exception.getMessage());
    }

    @Test
    @DisplayName("Should allow value of 1 as a valid boundary")
    void shouldAllowMinimumValidValue() {
        var quantity = Quantity.of(1);
        assertEquals(1, quantity.value());
    }

    @Test
    @DisplayName("Should create Quantity with a positive value")
    void shouldCreateWithPositiveValue() {
        var quantity = Quantity.of(5);
        assertEquals(5, quantity.value());
    }

    @Test
    @DisplayName("Should be equal and share hashCode when value matches")
    void shouldBeEqualWhenValueMatches() {
        assertEquals(Quantity.of(5), Quantity.of(5));
        assertEquals(Quantity.of(5).hashCode(), Quantity.of(5).hashCode());
    }

    @Test
    @DisplayName("Should not be equal when value differs")
    void shouldNotBeEqualWhenValueDiffers() {
        assertNotEquals(Quantity.of(5), Quantity.of(6));
    }

    @Test
    @DisplayName("Should include value in toString")
    void shouldIncludeValueInToString() {
        assertTrue(Quantity.of(5).toString().contains("5"));
    }
}