package de.alexandermora.erplite.domain.catalog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

@DisplayName("CatalogItem Domain Test")
class CatalogItemTest {

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw IllegalArgumentException when code is blank or null")
    void shouldThrowIllegalArgumentExceptionWhenCodeIsBlankOrNull(String code) {
        var exception = assertThrows(IllegalArgumentException.class, () -> new CatalogItem("1", code, "value", "description", 1, Map.of("Vale display", "Test")));
        assertEquals("Code cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should create CatalogItem successfully")
    void testCatalogItemCreation() {
        var item = new CatalogItem("1", "code1", "value1", "description1", 1, null);
        assertEquals("code1", item.getCode());
        assertEquals("value1", item.getValue());
        assertEquals("description1", item.getDescription());
        assertEquals(1, item.getDisplayOrder());
        assertTrue(item.isActive());
    }

    @Test
    @DisplayName("Should turn off CatalogItem status")
    void testTurnOffStatus() {
        var item = new CatalogItem("2", "code2", "value2", "description2", 2, null);
        item.turnOffStatus();
        assertFalse(item.isActive());
    }

    @Test
    @DisplayName("Should turn on CatalogItem status")
    void testTurnOnStatus() {
        var item = new CatalogItem("3", "code3", "value3", "description3", 3, null);
        item.turnOffStatus();
        item.turnOnStatus();
        assertTrue(item.isActive());
    }

    @Test
    @DisplayName("Should handle CatalogItem metadata correctly")
    void testMetadataHandling() {
        var item = new CatalogItem("4", "code4", "value4", "description4", 4, Map.of("key1", "value1"));
        assertTrue(item.hasMetadata("key1"));
        assertEquals("value1", item.getMetadata("key1"));
    }

    @Test
    @DisplayName("Should return null when metadata key is missing")
    void testGetMetadataReturnsNullForMissingKey() {
        var item = new CatalogItem("5", "code5", "value5", "description5", 5, Map.of("key1", "value1"));
        assertNull(item.getMetadata("missing"));
    }

    @Test
    @DisplayName("Should return false when metadata key is missing")
    void testHasMetadataReturnsFalseForMissingKey() {
        var item = new CatalogItem("6", "code6", "value6", "description6", 6, Map.of("key1", "value1"));
        assertFalse(item.hasMetadata("missing"));
    }

    @Test
    @DisplayName("Should be equal to itself")
    void testEqualsSameInstance() {
        var item = new CatalogItem("7", "code7", "value7", "description7", 7, null);
        assertEquals(item, item);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void testEqualsNull() {
        var item = new CatalogItem("8", "code8", "value8", "description8", 8, null);
        assertNotEquals(null, item);
    }

    @Test
    @DisplayName("Should not be equal to an instance of a different type")
    void testEqualsDifferentType() {
        var item = new CatalogItem("9", "code9", "value9", "description9", 9, null);
        assertNotEquals("not a CatalogItem", item);
    }

    @Test
    @DisplayName("Should be equal and share hashCode when all fields match")
    void testEqualsWithSameFieldValues() {
        var item1 = new CatalogItem("10", "code10", "value10", "description10", 10, Map.of("key1", "value1"));
        var item2 = new CatalogItem("10", "code10", "value10", "description10", 10, Map.of("key1", "value1"));
        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when id differs")
    void testNotEqualsWithDifferentId() {
        var item1 = new CatalogItem("11a", "code11", "value11", "description11", 11, Map.of("key1", "value1"));
        var item2 = new CatalogItem("11b", "code11", "value11", "description11", 11, Map.of("key1", "value1"));
        assertNotEquals(item1, item2);
    }

    @Test
    @DisplayName("Should not be equal when code differs")
    void testNotEqualsWithDifferentCode() {
        var item1 = new CatalogItem("12", "code12a", "value12", "description12", 12, Map.of("key1", "value1"));
        var item2 = new CatalogItem("12", "code12b", "value12", "description12", 12, Map.of("key1", "value1"));
        assertNotEquals(item1, item2);
    }

    @Test
    @DisplayName("Should not be equal when value differs")
    void testNotEqualsWithDifferentValue() {
        var item1 = new CatalogItem("13", "code13", "value13a", "description13", 13, Map.of("key1", "value1"));
        var item2 = new CatalogItem("13", "code13", "value13b", "description13", 13, Map.of("key1", "value1"));
        assertNotEquals(item1, item2);
    }

    @Test
    @DisplayName("Should not be equal when description differs")
    void testNotEqualsWithDifferentDescription() {
        var item1 = new CatalogItem("14", "code14", "value14", "description14a", 14, Map.of("key1", "value1"));
        var item2 = new CatalogItem("14", "code14", "value14", "description14b", 14, Map.of("key1", "value1"));
        assertNotEquals(item1, item2);
    }

    @Test
    @DisplayName("Should not be equal when displayOrder differs")
    void testNotEqualsWithDifferentDisplayOrder() {
        var item1 = new CatalogItem("15", "code15", "value15", "description15", 15, Map.of("key1", "value1"));
        var item2 = new CatalogItem("15", "code15", "value15", "description15", 16, Map.of("key1", "value1"));
        assertNotEquals(item1, item2);
    }

    @Test
    @DisplayName("Should not be equal when metadata differs")
    void testNotEqualsWithDifferentMetadata() {
        var item1 = new CatalogItem("16", "code16", "value16", "description16", 16, Map.of("key1", "value1"));
        var item2 = new CatalogItem("16", "code16", "value16", "description16", 16, Map.of("key2", "value2"));
        assertNotEquals(item1, item2);
    }

    @Test
    @DisplayName("Should not be equal when active status differs")
    void testNotEqualsWithDifferentActiveStatus() {
        var item1 = new CatalogItem("17", "code17", "value17", "description17", 17, Map.of("key1", "value1"));
        var item2 = new CatalogItem("17", "code17", "value17", "description17", 17, Map.of("key1", "value1"));
        item2.turnOffStatus();
        assertNotEquals(item1, item2);
    }

    @Test
    @DisplayName("Should include key fields in toString")
    void testToStringContainsKeyFields() {
        var item = new CatalogItem("18", "code18", "value18", "description18", 18, null);
        var result = item.toString();
        assertTrue(result.contains("code18"));
        assertTrue(result.contains("18"));
    }

}