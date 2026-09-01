package de.alexandermora.erplite.domain.order.events;

import de.alexandermora.erplite.domain.order.OrderId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderDelivered Domain Event Test")
class OrderDeliveredTest {

    @Test
    @DisplayName("Should hold constructor values and expose them via accessors")
    void shouldHoldValues() {
        var orderId = OrderId.generate();
        var timestamp = Instant.now();

        var event = new OrderDelivered(orderId, timestamp);

        assertEquals(orderId, event.orderId());
        assertEquals(timestamp, event.timestamp());
    }

    @Test
    @DisplayName("Should honor equals/hashCode/toString contract")
    void shouldHonorEqualsHashCodeToString() {
        var orderId = OrderId.generate();
        var timestamp = Instant.now();

        var first = new OrderDelivered(orderId, timestamp);
        var second = new OrderDelivered(orderId, timestamp);
        var different = new OrderDelivered(OrderId.generate(), timestamp);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, different);
        assertTrue(first.toString().contains("OrderDelivered"));
    }
}