package de.alexandermora.erplite.domain.catalog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CatalogType Domain Test")
class CatalogTypeTest {

    @Test
    @DisplayName("Should expose code and displayName")
    void shouldExposeCodeAndDisplayName() {
        assertEquals("PRODUCT_CATEGORIES", CatalogType.PRODUCT_CATEGORIES.getCode());
        assertEquals("Product Categories", CatalogType.PRODUCT_CATEGORIES.getDisplayName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Should throw IllegalArgumentException when code is null, empty or blank")
    void shouldThrowWhenCodeIsBlank(String code) {
        var exception = assertThrows(IllegalArgumentException.class, () -> CatalogType.fromCode(code));
        assertEquals("Catalog type code cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when code does not match any constant")
    void shouldThrowWhenCodeIsUnknown() {
        var exception = assertThrows(IllegalArgumentException.class, () -> CatalogType.fromCode("BOGUS"));
        assertTrue(exception.getMessage().startsWith("Invalid catalog type code: 'BOGUS'"));
    }

    @Test
    @DisplayName("Should be case-sensitive")
    void shouldBeCaseSensitive() {
        assertThrows(IllegalArgumentException.class, () -> CatalogType.fromCode("product_categories"));
    }

    @Test
    @DisplayName("Should resolve valid codes to their enum constant")
    void shouldResolveValidCodes() {
        assertEquals(CatalogType.PRODUCT_CATEGORIES, CatalogType.fromCode("PRODUCT_CATEGORIES"));
        assertEquals(CatalogType.ORDER_STATUSES, CatalogType.fromCode("ORDER_STATUSES"));
        assertEquals(CatalogType.PAYMENT_METHODS, CatalogType.fromCode("PAYMENT_METHODS"));
        assertEquals(CatalogType.SHIPPING_METHODS, CatalogType.fromCode("SHIPPING_METHODS"));
        assertEquals(CatalogType.COUNTRIES, CatalogType.fromCode("COUNTRIES"));
        assertEquals(CatalogType.CURRENCIES, CatalogType.fromCode("CURRENCIES"));
    }

    @Test
    @DisplayName("Should return true for a valid code")
    void shouldReturnTrueForValidCode() {
        assertTrue(CatalogType.isValid("COUNTRIES"));
    }

    @Test
    @DisplayName("Should return false for a null, blank or unknown code")
    void shouldReturnFalseForInvalidCode() {
        assertFalse(CatalogType.isValid(null));
        assertFalse(CatalogType.isValid(""));
        assertFalse(CatalogType.isValid("BOGUS"));
    }
}