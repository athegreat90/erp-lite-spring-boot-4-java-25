package de.alexandermora.erplite.domain.product.events;

import de.alexandermora.erplite.domain.product.ProductId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StockChanged Domain Event Test")
class StockChangedTest {

    @Test
    @DisplayName("Should expose the constructor arguments via accessors")
    void shouldExposeConstructorArgumentsViaAccessors() {
        var productId = ProductId.generate();
        var timestamp = Instant.now();

        var event = new StockChanged(productId, 10, 15, "restock", timestamp);

        assertEquals(productId, event.productId());
        assertEquals(10, event.oldStock());
        assertEquals(15, event.newStock());
        assertEquals("restock", event.reason());
        assertEquals(timestamp, event.timestamp());
    }

    @Test
    @DisplayName("Should honor equals/hashCode/toString contract")
    void shouldHonorEqualsHashCodeToStringContract() {
        var productId = ProductId.generate();
        var timestamp = Instant.now();

        var event1 = new StockChanged(productId, 10, 15, "restock", timestamp);
        var event2 = new StockChanged(productId, 10, 15, "restock", timestamp);
        var event3 = new StockChanged(productId, 10, 5, "sale", timestamp);

        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
        assertNotEquals(event1, event3);
        assertTrue(event1.toString().contains("StockChanged"));
    }
}