package de.alexandermora.erplite.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CategoryReference Domain Test")
class CategoryReferenceTest {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    @DisplayName("Should throw IllegalArgumentException when categoryId is null or blank")
    void shouldThrowIllegalArgumentExceptionWhenCategoryIdIsNullOrBlank(String categoryId) {
        var exception = assertThrows(IllegalArgumentException.class, () -> new CategoryReference(categoryId));
        assertEquals("categoryId must not be blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should create CategoryReference for a valid non-blank value")
    void shouldCreateCategoryReferenceForValidValue() {
        var reference = CategoryReference.of("cat-electronics");
        assertEquals("cat-electronics", reference.categoryId());
    }
}