package de.alexandermora.erplite.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SKU Domain Test")
class SKUTest {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "laptop-001",   // lowercase letters
            "LAPTOP001",    // missing dash
            "LAPTOP-01",    // too few digits
            "LAPTOP-0001",  // too many digits
            "LAPTOP-ABC",   // non-digit suffix
            "",             // empty string
            "-001"          // no letters before dash
    })
    @DisplayName("Should throw IllegalArgumentException for invalid SKU values")
    void shouldThrowIllegalArgumentExceptionForInvalidValues(String value) {
        var exception = assertThrows(IllegalArgumentException.class, () -> new SKU(value));
        assertTrue(exception.getMessage().startsWith("SKU must match pattern [A-Z]+-NNN:"));
    }

    @Test
    @DisplayName("Should create SKU for a valid single-word code")
    void shouldCreateSkuForValidSingleWordCode() {
        var sku = new SKU("LAPTOP-001");
        assertEquals("LAPTOP-001", sku.value());
    }

    @Test
    @DisplayName("Should create SKU for a valid short letter group")
    void shouldCreateSkuForValidShortLetterGroup() {
        var sku = SKU.of("AB-001");
        assertEquals("AB-001", sku.value());
    }
}