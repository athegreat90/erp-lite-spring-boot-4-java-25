package de.alexandermora.erplite.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderItemId Domain Test")
class OrderItemIdTest {

    @Test
    @DisplayName("Should throw NullPointerException when value is null")
    void shouldThrowNullPointerExceptionWhenValueIsNull() {
        assertThrows(NullPointerException.class, () -> new OrderItemId(null));
    }

    @Test
    @DisplayName("Should create OrderItemId with a given value")
    void shouldCreateOrderItemIdWithGivenValue() {
        var uuid = UUID.randomUUID();
        var orderItemId = new OrderItemId(uuid);
        assertEquals(uuid, orderItemId.value());
    }

    @Test
    @DisplayName("Should create OrderItemId via of()")
    void shouldCreateOrderItemIdViaOf() {
        var uuid = UUID.randomUUID();
        var orderItemId = OrderItemId.of(uuid);
        assertEquals(uuid, orderItemId.value());
    }

    @Test
    @DisplayName("Should throw NullPointerException when of() is called with null")
    void shouldThrowNullPointerExceptionWhenOfIsCalledWithNull() {
        assertThrows(NullPointerException.class, () -> OrderItemId.of(null));
    }

    @Test
    @DisplayName("Should generate unique OrderItemIds")
    void shouldGenerateUniqueOrderItemIds() {
        var first = OrderItemId.generate();
        var second = OrderItemId.generate();
        assertNotNull(first.value());
        assertNotEquals(first, second);
    }

    @Test
    @DisplayName("Should honor equals/hashCode/toString contract")
    void shouldHonorEqualsHashCodeToString() {
        var uuid = UUID.randomUUID();
        var first = new OrderItemId(uuid);
        var second = new OrderItemId(uuid);
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, OrderItemId.generate());
        assertTrue(first.toString().contains(uuid.toString()));
    }
}