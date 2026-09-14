package de.alexandermora.erplite.domain.views;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductView Record Test")
class ProductViewTest {

    @Test
    @DisplayName("Should hold constructor values and expose them via accessors")
    void shouldHoldValues() {
        var tags = List.of("laptop", "electronics");
        var specifications = Map.<String, Object>of("ram", "16GB");

        var productView = new ProductView("LAPTOP-001", "Dell XPS 15", "High-end laptop",
                1499.99, "$1,499.99", 10, "https://example.com/laptop.png", tags, specifications);

        assertEquals("LAPTOP-001", productView.sku());
        assertEquals("Dell XPS 15", productView.name());
        assertEquals("High-end laptop", productView.description());
        assertEquals(1499.99, productView.price());
        assertEquals("$1,499.99", productView.money());
        assertEquals(10, productView.stock());
        assertEquals("https://example.com/laptop.png", productView.imageUrl());
        assertEquals(tags, productView.tags());
        assertEquals(specifications, productView.specifications());
    }

    @Test
    @DisplayName("Should honor equals/hashCode/toString contract")
    void shouldHonorEqualsHashCodeToString() {
        var tags = List.of("laptop", "electronics");
        var specifications = Map.<String, Object>of("ram", "16GB");

        var first = new ProductView("LAPTOP-001", "Dell XPS 15", "High-end laptop",
                1499.99, "$1,499.99", 10, "https://example.com/laptop.png", tags, specifications);
        var second = new ProductView("LAPTOP-001", "Dell XPS 15", "High-end laptop",
                1499.99, "$1,499.99", 10, "https://example.com/laptop.png", tags, specifications);
        var different = new ProductView("KEYBOARD-001", "Mechanical Keyboard", "RGB keyboard",
                149.99, "$149.99", 25, "https://example.com/keyboard.png", List.of(), Map.of());

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, different);
        assertTrue(first.toString().contains("Dell XPS 15"));
    }
}
