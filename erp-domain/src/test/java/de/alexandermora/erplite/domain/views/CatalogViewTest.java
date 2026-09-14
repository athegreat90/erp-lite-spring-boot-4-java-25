package de.alexandermora.erplite.domain.views;

import de.alexandermora.erplite.commons.enums.CatalogType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CatalogView Record Test")
class CatalogViewTest {

    @Test
    @DisplayName("Should hold constructor values and expose them via accessors")
    void shouldHoldValues() {
        var createdAt = Instant.now();
        var updatedAt = Instant.now();
        var items = List.of(new ItemsView("ELECTRONICS", "Electronics", "Electronic devices", 1));

        var catalogView = new CatalogView(true, "Product Categories", "Catalog of product categories",
                CatalogType.PRODUCT_CATEGORIES, createdAt, updatedAt, items);

        assertTrue(catalogView.active());
        assertEquals("Product Categories", catalogView.name());
        assertEquals("Catalog of product categories", catalogView.description());
        assertEquals(CatalogType.PRODUCT_CATEGORIES, catalogView.type());
        assertEquals(createdAt, catalogView.createdAt());
        assertEquals(updatedAt, catalogView.updatedAt());
        assertEquals(items, catalogView.items());
    }

    @Test
    @DisplayName("Should honor equals/hashCode/toString contract")
    void shouldHonorEqualsHashCodeToString() {
        var createdAt = Instant.now();
        var updatedAt = Instant.now();
        var items = List.of(new ItemsView("ELECTRONICS", "Electronics", "Electronic devices", 1));

        var first = new CatalogView(true, "Product Categories", "Catalog of product categories",
                CatalogType.PRODUCT_CATEGORIES, createdAt, updatedAt, items);
        var second = new CatalogView(true, "Product Categories", "Catalog of product categories",
                CatalogType.PRODUCT_CATEGORIES, createdAt, updatedAt, items);
        var different = new CatalogView(false, "Order Statuses", "Catalog of order statuses",
                CatalogType.ORDER_STATUSES, createdAt, updatedAt, List.of());

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, different);
        assertTrue(first.toString().contains("Product Categories"));
    }
}
