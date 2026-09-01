package de.alexandermora.erplite.domain.catalog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Catalog Domain Test")
class CatalogTest {

    private static CatalogItem item(String id, String code) {
        return new CatalogItem(id, code, "value-" + code, "description-" + code, 1, null);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for catalogType before checking id")
    void shouldValidateCatalogTypeBeforeEntityId() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> new Catalog(null, null, "name", "description", List.of(), true));
        assertEquals("Catalog type cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when name is null")
    void shouldThrowWhenNameIsNull() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> new Catalog("1", CatalogType.COUNTRIES, null, "description", List.of(), true));
        assertEquals("Catalog name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should allow an empty (but non-null) name, despite the exception message wording")
    void shouldAllowEmptyName() {
        var catalog = new Catalog("1", CatalogType.COUNTRIES, "", "description", List.of(), true);
        assertEquals("", catalog.getName());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException from Entity when id is null but catalogType/name are valid")
    void shouldThrowWhenIdIsNull() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> new Catalog(null, CatalogType.COUNTRIES, "name", "description", List.of(), true));
        assertEquals("Entity ID cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NullPointerException when items is null")
    void shouldThrowWhenItemsIsNull() {
        assertThrows(NullPointerException.class,
                () -> new Catalog("1", CatalogType.COUNTRIES, "name", "description", null, true));
    }

    @Test
    @DisplayName("Should create Catalog successfully with valid arguments")
    void shouldCreateSuccessfully() {
        var catalog = new Catalog("1", CatalogType.COUNTRIES, "name", "description", List.of(item("i1", "c1")), true);
        assertEquals(CatalogType.COUNTRIES, catalog.getCatalogType());
        assertEquals("name", catalog.getName());
        assertEquals("description", catalog.getDescription());
        assertEquals(1, catalog.getItems().size());
        assertTrue(catalog.isActive());
    }

    @Test
    @DisplayName("Should not be affected by mutating the original list after construction")
    void shouldDefensivelyCopyItems() {
        var mutableList = new ArrayList<CatalogItem>();
        mutableList.add(item("i1", "c1"));
        var catalog = new Catalog("1", CatalogType.COUNTRIES, "name", "description", mutableList, true);

        mutableList.add(item("i2", "c2"));

        assertEquals(1, catalog.getItems().size());
    }

    @Test
    @DisplayName("Should return an unmodifiable list from getItems()")
    void shouldReturnUnmodifiableItems() {
        var catalog = new Catalog("1", CatalogType.COUNTRIES, "name", "description", List.of(item("i1", "c1")), true);
        var items = catalog.getItems();
        assertThrows(UnsupportedOperationException.class, () -> items.add(item("i2", "c2")));
    }

    @Test
    @DisplayName("Should find an item by matching code")
    void shouldFindItemByCode() {
        var catalog = new Catalog("1", CatalogType.COUNTRIES, "name", "description", List.of(item("i1", "c1")), true);
        var found = catalog.findItemByCode("c1");
        assertTrue(found.isPresent());
        assertEquals("c1", found.get().getCode());
    }

    @Test
    @DisplayName("Should return empty Optional when no item matches the code")
    void shouldReturnEmptyWhenCodeNotFound() {
        var catalog = new Catalog("1", CatalogType.COUNTRIES, "name", "description", List.of(item("i1", "c1")), true);
        assertTrue(catalog.findItemByCode("missing").isEmpty());
    }

    @Test
    @DisplayName("Should return empty Optional when items list is empty")
    void shouldReturnEmptyWhenItemsIsEmpty() {
        var catalog = new Catalog("1", CatalogType.COUNTRIES, "name", "description", List.of(), true);
        assertTrue(catalog.findItemByCode("c1").isEmpty());
    }

    @Test
    @DisplayName("Should return empty Optional (not throw) when code is null")
    void shouldReturnEmptyWhenCodeIsNull() {
        var catalog = new Catalog("1", CatalogType.COUNTRIES, "name", "description", List.of(item("i1", "c1")), true);
        assertTrue(catalog.findItemByCode(null).isEmpty());
    }

    @Test
    @DisplayName("Should return true when the catalog contains an item with the given code")
    void shouldContainItem() {
        var catalog = new Catalog("1", CatalogType.COUNTRIES, "name", "description", List.of(item("i1", "c1")), true);
        assertTrue(catalog.containsItem("c1"));
    }

    @Test
    @DisplayName("Should return false when the catalog does not contain an item with the given code")
    void shouldNotContainItem() {
        var catalog = new Catalog("1", CatalogType.COUNTRIES, "name", "description", List.of(item("i1", "c1")), true);
        assertFalse(catalog.containsItem("missing"));
    }

    @Test
    @DisplayName("Should return empty list when items is empty for findActiveItems")
    void shouldReturnEmptyActiveItemsWhenEmpty() {
        var catalog = new Catalog("1", CatalogType.COUNTRIES, "name", "description", List.of(), true);
        assertTrue(catalog.findActiveItems().isEmpty());
    }

    @Test
    @DisplayName("Should filter only active items")
    void shouldFilterActiveItems() {
        var active = item("i1", "c1");
        var inactive = item("i2", "c2");
        inactive.turnOffStatus();

        var catalog = new Catalog("1", CatalogType.COUNTRIES, "name", "description", List.of(active, inactive), true);
        var activeItems = catalog.findActiveItems();

        assertEquals(1, activeItems.size());
        assertEquals("c1", activeItems.get(0).getCode());
    }

    @Test
    @DisplayName("Should return empty list when all items are inactive")
    void shouldReturnEmptyWhenAllInactive() {
        var inactive = item("i1", "c1");
        inactive.turnOffStatus();

        var catalog = new Catalog("1", CatalogType.COUNTRIES, "name", "description", List.of(inactive), true);
        assertTrue(catalog.findActiveItems().isEmpty());
    }

    @Test
    @DisplayName("Should return all items when all are active")
    void shouldReturnAllWhenAllActive() {
        var catalog = new Catalog("1", CatalogType.COUNTRIES, "name", "description",
                List.of(item("i1", "c1"), item("i2", "c2")), true);
        assertEquals(2, catalog.findActiveItems().size());
    }

    @Test
    @DisplayName("Should return all items regardless of active status via findAll")
    void shouldReturnAllItemsRegardlessOfStatus() {
        var active = item("i1", "c1");
        var inactive = item("i2", "c2");
        inactive.turnOffStatus();

        var catalog = new Catalog("1", CatalogType.COUNTRIES, "name", "description", List.of(active, inactive), true);
        assertEquals(2, catalog.findAll().size());
    }

    @Test
    @DisplayName("Should be equal and share hashCode when all fields match")
    void shouldBeEqualWhenFieldsMatch() {
        var catalog1 = new Catalog("1", CatalogType.COUNTRIES, "name", "description", List.of(item("i1", "c1")), true);
        var catalog2 = new Catalog("1", CatalogType.COUNTRIES, "name", "description", List.of(item("i1", "c1")), true);
        assertEquals(catalog1, catalog2);
        assertEquals(catalog1.hashCode(), catalog2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when id differs")
    void shouldNotBeEqualWhenIdDiffers() {
        var catalog1 = new Catalog("1", CatalogType.COUNTRIES, "name", "description", List.of(item("i1", "c1")), true);
        var catalog2 = new Catalog("2", CatalogType.COUNTRIES, "name", "description", List.of(item("i1", "c1")), true);
        assertNotEquals(catalog1, catalog2);
    }

    @Test
    @DisplayName("Should not be equal when catalogType differs")
    void shouldNotBeEqualWhenCatalogTypeDiffers() {
        var catalog1 = new Catalog("1", CatalogType.COUNTRIES, "name", "description", List.of(item("i1", "c1")), true);
        var catalog2 = new Catalog("1", CatalogType.CURRENCIES, "name", "description", List.of(item("i1", "c1")), true);
        assertNotEquals(catalog1, catalog2);
    }

    @Test
    @DisplayName("Should not be equal when name differs")
    void shouldNotBeEqualWhenNameDiffers() {
        var catalog1 = new Catalog("1", CatalogType.COUNTRIES, "name1", "description", List.of(item("i1", "c1")), true);
        var catalog2 = new Catalog("1", CatalogType.COUNTRIES, "name2", "description", List.of(item("i1", "c1")), true);
        assertNotEquals(catalog1, catalog2);
    }

    @Test
    @DisplayName("Should not be equal when items differs")
    void shouldNotBeEqualWhenItemsDiffers() {
        var catalog1 = new Catalog("1", CatalogType.COUNTRIES, "name", "description", List.of(item("i1", "c1")), true);
        var catalog2 = new Catalog("1", CatalogType.COUNTRIES, "name", "description", List.of(item("i1", "c2")), true);
        assertNotEquals(catalog1, catalog2);
    }

    @Test
    @DisplayName("Should not be equal when isActive differs")
    void shouldNotBeEqualWhenActiveStatusDiffers() {
        var catalog1 = new Catalog("1", CatalogType.COUNTRIES, "name", "description", List.of(item("i1", "c1")), true);
        var catalog2 = new Catalog("1", CatalogType.COUNTRIES, "name", "description", List.of(item("i1", "c1")), false);
        assertNotEquals(catalog1, catalog2);
    }

    @Test
    @DisplayName("Should include name in toString")
    void shouldIncludeNameInToString() {
        var catalog = new Catalog("1", CatalogType.COUNTRIES, "name", "description", List.of(item("i1", "c1")), true);
        assertTrue(catalog.toString().contains("name"));
    }
}