package de.alexandermora.erplite.domain.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Entity Domain Test")
class EntityTest {

    private static class TestEntity extends Entity<String> {
        TestEntity(String id) {
            super(id);
        }
    }

    private static class OtherTestEntity extends Entity<String> {
        OtherTestEntity(String id) {
            super(id);
        }
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when id is null")
    void shouldThrowIllegalArgumentExceptionWhenIdIsNull() {
        var exception = assertThrows(IllegalArgumentException.class, () -> new TestEntity(null));
        assertEquals("Entity ID cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should create entity successfully with a non-null id")
    void shouldCreateEntitySuccessfully() {
        var entity = new TestEntity("1");
        assertEquals("1", entity.getId());
    }

    @Test
    @DisplayName("Should be equal to itself")
    void testEqualsSameInstance() {
        var entity = new TestEntity("1");
        assertEquals(entity, entity);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void testEqualsNull() {
        var entity = new TestEntity("1");
        // assertNotEquals(null, entity) would short-circuit inside JUnit's Objects.equals-style
        // check without ever calling entity.equals(null), so call it directly to hit that branch.
        assertFalse(entity.equals(null));
    }

    @Test
    @DisplayName("Should not be equal to an instance of a different class")
    void testEqualsDifferentClass() {
        var entity = new TestEntity("1");
        var other = new OtherTestEntity("1");
        assertNotEquals(entity, other);
    }

    @Test
    @DisplayName("Should not be equal when id differs")
    void testEqualsDifferentId() {
        var entity1 = new TestEntity("1");
        var entity2 = new TestEntity("2");
        assertNotEquals(entity1, entity2);
    }

    @Test
    @DisplayName("Should be equal and share hashCode when id matches")
    void testEqualsSameId() {
        var entity1 = new TestEntity("1");
        var entity2 = new TestEntity("1");
        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }
}