package de.alexandermora.erplite.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Email Domain Test")
class EmailTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Should throw IllegalArgumentException when value is null, empty or blank")
    void shouldThrowWhenValueIsBlank(String value) {
        var exception = assertThrows(IllegalArgumentException.class, () -> new Email(value));
        assertEquals("value must not be blank", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"missing-at.com", "no-domain@", "bad chars@example.com", "user@example.c"})
    @DisplayName("Should throw IllegalArgumentException for an invalid email format")
    void shouldThrowWhenFormatIsInvalid(String value) {
        var exception = assertThrows(IllegalArgumentException.class, () -> new Email(value));
        assertTrue(exception.getMessage().startsWith("Invalid email format"));
    }

    @Test
    @DisplayName("Should allow a 2-character TLD as a valid boundary")
    void shouldAllowTwoCharacterTld() {
        var email = Email.of("user@example.co");
        assertEquals("user@example.co", email.value());
    }

    @Test
    @DisplayName("Should create Email with a valid address")
    void shouldCreateWithValidAddress() {
        var email = Email.of("user@example.com");
        assertEquals("user@example.com", email.value());
    }

    @Test
    @DisplayName("Should be equal and share hashCode when value matches")
    void shouldBeEqualWhenValueMatches() {
        assertEquals(Email.of("user@example.com"), Email.of("user@example.com"));
        assertEquals(Email.of("user@example.com").hashCode(), Email.of("user@example.com").hashCode());
    }

    @Test
    @DisplayName("Should not be equal when value differs")
    void shouldNotBeEqualWhenValueDiffers() {
        assertNotEquals(Email.of("user1@example.com"), Email.of("user2@example.com"));
    }

    @Test
    @DisplayName("Should include value in toString")
    void shouldIncludeValueInToString() {
        assertTrue(Email.of("user@example.com").toString().contains("user@example.com"));
    }
}