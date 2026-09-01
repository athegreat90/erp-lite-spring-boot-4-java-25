package de.alexandermora.erplite.domain.order.events;

import de.alexandermora.erplite.domain.order.OrderId;
import de.alexandermora.erplite.domain.shared.CustomerId;
import de.alexandermora.erplite.domain.shared.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderCreated Domain Event Test")
class OrderCreatedTest {

    @Test
    @DisplayName("Should hold constructor values and expose them via accessors")
    void shouldHoldValues() {
        var orderId = OrderId.generate();
        var customerId = CustomerId.of(1L);
        var totalAmount = Money.of(BigDecimal.TEN, Currency.getInstance("USD"));
        var timestamp = Instant.now();

        var event = new OrderCreated(orderId, customerId, "John Doe", totalAmount, timestamp);

        assertEquals(orderId, event.orderId());
        assertEquals(customerId, event.customerId());
        assertEquals("John Doe", event.customerName());
        assertEquals(totalAmount, event.totalAmount());
        assertEquals(timestamp, event.timestamp());
    }

    @Test
    @DisplayName("Should honor equals/hashCode/toString contract")
    void shouldHonorEqualsHashCodeToString() {
        var orderId = OrderId.generate();
        var customerId = CustomerId.of(1L);
        var totalAmount = Money.of(BigDecimal.TEN, Currency.getInstance("USD"));
        var timestamp = Instant.now();

        var first = new OrderCreated(orderId, customerId, "John Doe", totalAmount, timestamp);
        var second = new OrderCreated(orderId, customerId, "John Doe", totalAmount, timestamp);
        var different = new OrderCreated(OrderId.generate(), customerId, "John Doe", totalAmount, timestamp);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, different);
        assertTrue(first.toString().contains("John Doe"));
    }
}