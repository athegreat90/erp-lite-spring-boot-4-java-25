package de.alexandermora.erplite.domain.views;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemsView Record Test")
class ItemsViewTest {

    @Test
    @DisplayName("Should hold constructor values and expose them via accessors")
    void shouldHoldValues() {
        var itemsView = new ItemsView("ELECTRONICS", "Electronics", "Electronic devices", 1);

        assertEquals("ELECTRONICS", itemsView.code());
        assertEquals("Electronics", itemsView.value());
        assertEquals("Electronic devices", itemsView.description());
        assertEquals(1, itemsView.displayOrder());
    }

    @Test
    @DisplayName("Should honor equals/hashCode/toString contract")
    void shouldHonorEqualsHashCodeToString() {
        var first = new ItemsView("ELECTRONICS", "Electronics", "Electronic devices", 1);
        var second = new ItemsView("ELECTRONICS", "Electronics", "Electronic devices", 1);
        var different = new ItemsView("FURNITURE", "Furniture", "Home furniture", 2);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, different);
        assertTrue(first.toString().contains("Electronics"));
    }
}
