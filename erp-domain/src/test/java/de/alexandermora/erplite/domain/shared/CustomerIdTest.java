package de.alexandermora.erplite.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CustomerId Domain Test")
class CustomerIdTest {

    @Test
    @DisplayName("Should throw NullPointerException when value is null")
    void shouldThrowWhenValueIsNull() {
        assertThrows(NullPointerException.class, () -> new CustomerId(null));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    @DisplayName("Should throw IllegalArgumentException when value is not greater than 0")
    void shouldThrowWhenValueNotPositive(long value) {
        var exception = assertThrows(IllegalArgumentException.class, () -> new CustomerId(value));
        assertEquals("value must be > 0", exception.getMessage());
    }

    @Test
    @DisplayName("Should allow value of 1 as a valid boundary")
    void shouldAllowMinimumValidValue() {
        var customerId = CustomerId.of(1L);
        assertEquals(1L, customerId.value());
    }

    @Test
    @DisplayName("Should create CustomerId via of()")
    void shouldCreateViaOf() {
        var customerId = CustomerId.of(42L);
        assertEquals(42L, customerId.value());
    }

    @Test
    @DisplayName("Should be equal and share hashCode when value matches")
    void shouldBeEqualWhenValueMatches() {
        assertEquals(CustomerId.of(42L), CustomerId.of(42L));
        assertEquals(CustomerId.of(42L).hashCode(), CustomerId.of(42L).hashCode());
    }

    @Test
    @DisplayName("Should not be equal when value differs")
    void shouldNotBeEqualWhenValueDiffers() {
        assertNotEquals(CustomerId.of(42L), CustomerId.of(43L));
    }

    @Test
    @DisplayName("Should include value in toString")
    void shouldIncludeValueInToString() {
        assertTrue(CustomerId.of(42L).toString().contains("42"));
    }
}