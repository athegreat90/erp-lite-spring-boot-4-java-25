package de.alexandermora.erplite.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderId Domain Test")
class OrderIdTest {

    @Test
    @DisplayName("Should throw NullPointerException when value is null")
    void shouldThrowNullPointerExceptionWhenValueIsNull() {
        assertThrows(NullPointerException.class, () -> new OrderId(null));
    }

    @Test
    @DisplayName("Should create OrderId with a given value")
    void shouldCreateOrderIdWithGivenValue() {
        var uuid = UUID.randomUUID();
        var orderId = new OrderId(uuid);
        assertEquals(uuid, orderId.value());
    }

    @Test
    @DisplayName("Should create OrderId via of()")
    void shouldCreateOrderIdViaOf() {
        var uuid = UUID.randomUUID();
        var orderId = OrderId.of(uuid);
        assertEquals(uuid, orderId.value());
    }

    @Test
    @DisplayName("Should throw NullPointerException when of() is called with null")
    void shouldThrowNullPointerExceptionWhenOfIsCalledWithNull() {
        assertThrows(NullPointerException.class, () -> OrderId.of(null));
    }

    @Test
    @DisplayName("Should generate unique OrderIds")
    void shouldGenerateUniqueOrderIds() {
        var first = OrderId.generate();
        var second = OrderId.generate();
        assertNotNull(first.value());
        assertNotEquals(first, second);
    }

    @Test
    @DisplayName("Should honor equals/hashCode/toString contract")
    void shouldHonorEqualsHashCodeToString() {
        var uuid = UUID.randomUUID();
        var first = new OrderId(uuid);
        var second = new OrderId(uuid);
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, OrderId.generate());
        assertTrue(first.toString().contains(uuid.toString()));
    }
}