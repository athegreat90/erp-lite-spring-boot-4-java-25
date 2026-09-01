package de.alexandermora.erplite.domain.product.events;

import de.alexandermora.erplite.domain.product.ProductId;
import de.alexandermora.erplite.domain.product.ProductName;
import de.alexandermora.erplite.domain.product.SKU;
import de.alexandermora.erplite.domain.shared.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductCreated Domain Event Test")
class ProductCreatedTest {

    @Test
    @DisplayName("Should expose the constructor arguments via accessors")
    void shouldExposeConstructorArgumentsViaAccessors() {
        var productId = ProductId.generate();
        var sku = SKU.of("LAPTOP-001");
        var name = ProductName.of("Laptop");
        var price = Money.of(new BigDecimal("999.99"), Currency.getInstance("USD"));
        var timestamp = Instant.now();

        var event = new ProductCreated(productId, sku, name, price, timestamp);

        assertEquals(productId, event.productId());
        assertEquals(sku, event.sku());
        assertEquals(name, event.name());
        assertEquals(price, event.price());
        assertEquals(timestamp, event.timestamp());
    }

    @Test
    @DisplayName("Should honor equals/hashCode/toString contract")
    void shouldHonorEqualsHashCodeToStringContract() {
        var productId = ProductId.generate();
        var sku = SKU.of("LAPTOP-001");
        var name = ProductName.of("Laptop");
        var price = Money.of(new BigDecimal("999.99"), Currency.getInstance("USD"));
        var timestamp = Instant.now();

        var event1 = new ProductCreated(productId, sku, name, price, timestamp);
        var event2 = new ProductCreated(productId, sku, name, price, timestamp);
        var event3 = new ProductCreated(ProductId.generate(), sku, name, price, timestamp);

        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
        assertNotEquals(event1, event3);
        assertTrue(event1.toString().contains("ProductCreated"));
    }
}