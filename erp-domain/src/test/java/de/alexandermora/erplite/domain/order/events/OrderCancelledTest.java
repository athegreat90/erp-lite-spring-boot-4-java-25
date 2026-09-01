package de.alexandermora.erplite.domain.order.events;

import de.alexandermora.erplite.domain.order.OrderId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderCancelled Domain Event Test")
class OrderCancelledTest {

    @Test
    @DisplayName("Should hold constructor values and expose them via accessors")
    void shouldHoldValues() {
        var orderId = OrderId.generate();
        var timestamp = Instant.now();

        var event = new OrderCancelled(orderId, "out of stock", timestamp);

        assertEquals(orderId, event.orderId());
        assertEquals("out of stock", event.reason());
        assertEquals(timestamp, event.timestamp());
    }

    @Test
    @DisplayName("Should honor equals/hashCode/toString contract")
    void shouldHonorEqualsHashCodeToString() {
        var orderId = OrderId.generate();
        var timestamp = Instant.now();

        var first = new OrderCancelled(orderId, "out of stock", timestamp);
        var second = new OrderCancelled(orderId, "out of stock", timestamp);
        var different = new OrderCancelled(orderId, "customer request", timestamp);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, different);
        assertTrue(first.toString().contains("out of stock"));
    }
}