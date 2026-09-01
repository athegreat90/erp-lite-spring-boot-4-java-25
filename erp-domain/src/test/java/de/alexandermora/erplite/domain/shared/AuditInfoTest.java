package de.alexandermora.erplite.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AuditInfo Domain Test")
class AuditInfoTest {

    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Should throw IllegalArgumentException when createdBy is null, empty or blank")
    void shouldThrowWhenCreatedByIsBlank(String createdBy) {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> new AuditInfo(createdBy, NOW, NOW));
        assertEquals("createdBy must not be blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NullPointerException when createdAt is null")
    void shouldThrowWhenCreatedAtIsNull() {
        assertThrows(NullPointerException.class, () -> new AuditInfo("user", null, NOW));
    }

    @Test
    @DisplayName("Should throw NullPointerException when updatedAt is null")
    void shouldThrowWhenUpdatedAtIsNull() {
        assertThrows(NullPointerException.class, () -> new AuditInfo("user", NOW, null));
    }

    @Test
    @DisplayName("Should create AuditInfo with matching createdAt and updatedAt via create()")
    void shouldCreateWithMatchingTimestamps() {
        var auditInfo = AuditInfo.create("user", NOW);
        assertEquals("user", auditInfo.createdBy());
        assertEquals(NOW, auditInfo.createdAt());
        assertEquals(NOW, auditInfo.updatedAt());
    }

    @Test
    @DisplayName("Should update timestamp while preserving createdBy and createdAt")
    void shouldUpdateTimestamp() {
        var original = new AuditInfo("user", NOW, NOW);
        var updated = original.updateTimestamp();

        assertEquals(original.createdBy(), updated.createdBy());
        assertEquals(original.createdAt(), updated.createdAt());
        assertFalse(updated.updatedAt().isBefore(original.updatedAt()));
    }

    @Test
    @DisplayName("Should be equal and share hashCode when all fields match")
    void shouldBeEqualWhenFieldsMatch() {
        var info1 = new AuditInfo("user", NOW, NOW);
        var info2 = new AuditInfo("user", NOW, NOW);
        assertEquals(info1, info2);
        assertEquals(info1.hashCode(), info2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when createdBy differs")
    void shouldNotBeEqualWhenCreatedByDiffers() {
        var info1 = new AuditInfo("user1", NOW, NOW);
        var info2 = new AuditInfo("user2", NOW, NOW);
        assertNotEquals(info1, info2);
    }

    @Test
    @DisplayName("Should include createdBy in toString")
    void shouldIncludeCreatedByInToString() {
        assertTrue(new AuditInfo("user", NOW, NOW).toString().contains("user"));
    }
}