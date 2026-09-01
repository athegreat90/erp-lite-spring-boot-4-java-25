package de.alexandermora.erplite.domain.product.events;

import de.alexandermora.erplite.domain.product.ProductId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductUpdated Domain Event Test")
class ProductUpdatedTest {

    @Test
    @DisplayName("Should expose the constructor arguments via accessors")
    void shouldExposeConstructorArgumentsViaAccessors() {
        var productId = ProductId.generate();
        var timestamp = Instant.now();

        var event = new ProductUpdated(productId, timestamp);

        assertEquals(productId, event.productId());
        assertEquals(timestamp, event.timestamp());
    }

    @Test
    @DisplayName("Should honor equals/hashCode/toString contract")
    void shouldHonorEqualsHashCodeToStringContract() {
        var productId = ProductId.generate();
        var timestamp = Instant.now();

        var event1 = new ProductUpdated(productId, timestamp);
        var event2 = new ProductUpdated(productId, timestamp);
        var event3 = new ProductUpdated(ProductId.generate(), timestamp);

        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
        assertNotEquals(event1, event3);
        assertTrue(event1.toString().contains("ProductUpdated"));
    }
}