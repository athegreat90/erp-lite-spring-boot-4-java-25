package de.alexandermora.erplite.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MyBusinessException Domain Test")
class MyBusinessExceptionTest {

    @Test
    @DisplayName("Should expose the given message and cause")
    void shouldExposeMessageAndCause() {
        var cause = new IllegalStateException("boom");
        var exception = new MyBusinessException("something failed", cause);

        assertEquals("something failed", exception.getMessage());
        assertSame(cause, exception.getCause());
        assertInstanceOf(RuntimeException.class, exception);
    }
}
