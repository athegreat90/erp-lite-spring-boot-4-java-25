package de.alexandermora.erplite.domain.order;

import de.alexandermora.erplite.domain.product.CategoryReference;
import de.alexandermora.erplite.domain.product.Product;
import de.alexandermora.erplite.domain.product.ProductName;
import de.alexandermora.erplite.domain.product.SKU;
import de.alexandermora.erplite.domain.product.Stock;
import de.alexandermora.erplite.domain.shared.Money;
import de.alexandermora.erplite.domain.shared.Quantity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderItem Domain Test")
class OrderItemTest {

    private static Product createProduct(int stockQty, boolean active, String currencyCode) {
        var product = Product.create(
                SKU.of("LAPTOP-001"),
                ProductName.of("Laptop"),
                "A laptop",
                Money.of(BigDecimal.valueOf(1000), Currency.getInstance(currencyCode)),
                Stock.of(stockQty),
                CategoryReference.of("cat-electronics"),
                null,
                "tester"
        );
        if (!active) {
            product.deactivate();
        }
        return product;
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when no-arg constructor is invoked")
    void shouldThrowWhenNoArgConstructorInvoked() throws Exception {
        var constructor = OrderItem.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        var exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        assertEquals("Entity ID cannot be null", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when product is null")
    void shouldThrowIllegalArgumentExceptionWhenProductIsNull() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> OrderItem.from(null, Quantity.of(1)));
        assertEquals("Product cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when quantity is null")
    void shouldThrowIllegalArgumentExceptionWhenQuantityIsNull() {
        var product = createProduct(10, true, "USD");
        var exception = assertThrows(IllegalArgumentException.class,
                () -> OrderItem.from(product, null));
        assertEquals("Quantity cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when product is inactive")
    void shouldThrowIllegalArgumentExceptionWhenProductIsInactive() {
        var product = createProduct(10, false, "USD");
        var exception = assertThrows(IllegalArgumentException.class,
                () -> OrderItem.from(product, Quantity.of(1)));
        assertTrue(exception.getMessage().contains(product.getSku().value()));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when stock is insufficient")
    void shouldThrowIllegalArgumentExceptionWhenStockInsufficient() {
        var product = createProduct(1, true, "USD");
        var exception = assertThrows(IllegalArgumentException.class,
                () -> OrderItem.from(product, Quantity.of(2)));
        assertTrue(exception.getMessage().contains("Insufficient stock"));
    }

    @Test
    @DisplayName("Should create OrderItem when stock exactly matches required quantity")
    void shouldCreateOrderItemWithExactStockMatch() {
        var product = createProduct(2, true, "USD");
        var item = OrderItem.from(product, Quantity.of(2));

        assertNotNull(item.getId());
        assertEquals(product.getId(), item.getProductReference());
        assertEquals(product.getName().value(), item.getProductName());
        assertEquals(product.getPrice(), item.getUnitPrice());
        assertEquals(product.getPrice().multiply(Quantity.of(2)), item.getSubtotal());
    }

    @Test
    @DisplayName("Should create OrderItem when stock surplus is available")
    void shouldCreateOrderItemWithSurplusStock() {
        var product = createProduct(10, true, "USD");
        var item = OrderItem.from(product, Quantity.of(3));

        assertEquals(product.getPrice().multiply(Quantity.of(3)), item.getSubtotal());
    }

    @Test
    @DisplayName("Should calculate subtotal as unitPrice * quantity")
    void shouldCalculateSubtotal() {
        var product = createProduct(10, true, "USD");
        var item = OrderItem.from(product, Quantity.of(4));

        assertEquals(product.getPrice().multiply(Quantity.of(4)), item.calculateSubtotal());
    }
}