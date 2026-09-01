package de.alexandermora.erplite.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderNumber Domain Test")
class OrderNumberTest {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"ORD-25-001", "ORD-2025-1", "ord-2025-001", "ORD-2025-0001", "ORDER-2025-001", "ORD-2025-abc"})
    @DisplayName("Should throw IllegalArgumentException for invalid order number values")
    void shouldThrowIllegalArgumentExceptionForInvalidValues(String value) {
        assertThrows(IllegalArgumentException.class, () -> new OrderNumber(value));
    }

    @Test
    @DisplayName("Should create OrderNumber for a valid value")
    void shouldCreateOrderNumberForValidValue() {
        var orderNumber = new OrderNumber("ORD-2025-001");
        assertEquals("ORD-2025-001", orderNumber.value());
    }

    @Test
    @DisplayName("Should create OrderNumber via of()")
    void shouldCreateOrderNumberViaOf() {
        var orderNumber = OrderNumber.of("ORD-2025-999");
        assertEquals("ORD-2025-999", orderNumber.value());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 1000})
    @DisplayName("Should throw IllegalArgumentException when sequence is out of range")
    void shouldThrowIllegalArgumentExceptionWhenSequenceOutOfRange(int sequence) {
        var exception = assertThrows(IllegalArgumentException.class, () -> OrderNumber.generate(sequence));
        assertEquals("Sequence must be between 1 and 999", exception.getMessage());
    }

    @Test
    @DisplayName("Should generate OrderNumber for lower boundary sequence")
    void shouldGenerateForLowerBoundarySequence() {
        var orderNumber = OrderNumber.generate(1);
        assertEquals("ORD-" + Year.now().getValue() + "-001", orderNumber.value());
    }

    @Test
    @DisplayName("Should generate OrderNumber for upper boundary sequence")
    void shouldGenerateForUpperBoundarySequence() {
        var orderNumber = OrderNumber.generate(999);
        assertEquals("ORD-" + Year.now().getValue() + "-999", orderNumber.value());
    }

    @Test
    @DisplayName("Should zero-pad sequence in generated OrderNumber")
    void shouldZeroPadSequence() {
        var orderNumber = OrderNumber.generate(42);
        assertEquals("ORD-" + Year.now().getValue() + "-042", orderNumber.value());
    }

    @Test
    @DisplayName("Should honor equals/hashCode/toString contract")
    void shouldHonorEqualsHashCodeToString() {
        var first = new OrderNumber("ORD-2025-001");
        var second = new OrderNumber("ORD-2025-001");
        var different = new OrderNumber("ORD-2025-002");
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, different);
        assertTrue(first.toString().contains("ORD-2025-001"));
    }
}