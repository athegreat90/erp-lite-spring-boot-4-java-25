package de.alexandermora.erplite.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductId Domain Test")
class ProductIdTest {

    @Test
    @DisplayName("Should throw NullPointerException when value is null")
    void shouldThrowNullPointerExceptionWhenValueIsNull() {
        assertThrows(NullPointerException.class, () -> new ProductId(null));
    }

    @Test
    @DisplayName("Should create ProductId via canonical constructor")
    void shouldCreateProductIdViaCanonicalConstructor() {
        var uuid = UUID.randomUUID();
        var productId = new ProductId(uuid);
        assertEquals(uuid, productId.value());
    }

    @Test
    @DisplayName("Should create ProductId via of factory")
    void shouldCreateProductIdViaOfFactory() {
        var uuid = UUID.randomUUID();
        var productId = ProductId.of(uuid);
        assertEquals(uuid, productId.value());
    }

    @Test
    @DisplayName("Should throw NullPointerException when of is called with null")
    void shouldThrowNullPointerExceptionWhenOfIsCalledWithNull() {
        assertThrows(NullPointerException.class, () -> ProductId.of(null));
    }

    @Test
    @DisplayName("Should generate distinct ProductIds")
    void shouldGenerateDistinctProductIds() {
        var first = ProductId.generate();
        var second = ProductId.generate();
        assertNotNull(first.value());
        assertNotEquals(first, second);
    }

    @Test
    @DisplayName("Should be equal and share hashCode for the same value")
    void shouldBeEqualAndShareHashCodeForSameValue() {
        var uuid = UUID.randomUUID();
        var first = new ProductId(uuid);
        var second = new ProductId(uuid);
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.toString().contains(uuid.toString()));
    }
}