package de.alexandermora.erplite.domain.order;

import de.alexandermora.erplite.domain.order.events.OrderCancelled;
import de.alexandermora.erplite.domain.order.events.OrderConfirmed;
import de.alexandermora.erplite.domain.order.events.OrderCreated;
import de.alexandermora.erplite.domain.order.events.OrderDelivered;
import de.alexandermora.erplite.domain.order.events.OrderShipped;
import de.alexandermora.erplite.domain.product.CategoryReference;
import de.alexandermora.erplite.domain.product.Product;
import de.alexandermora.erplite.domain.product.ProductId;
import de.alexandermora.erplite.domain.product.ProductName;
import de.alexandermora.erplite.domain.product.SKU;
import de.alexandermora.erplite.domain.product.Stock;
import de.alexandermora.erplite.domain.shared.CustomerId;
import de.alexandermora.erplite.domain.shared.Money;
import de.alexandermora.erplite.domain.shared.Quantity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order Domain Test")
class OrderTest {

    private static Product product(String sku, int stockQty, String currencyCode) {
        return Product.create(
                SKU.of(sku),
                ProductName.of("Product " + sku),
                "description",
                Money.of(BigDecimal.valueOf(100), Currency.getInstance(currencyCode)),
                Stock.of(stockQty),
                CategoryReference.of("cat-electronics"),
                null,
                "tester"
        );
    }

    private static OrderItem item(String sku, int stockQty, int quantity, String currencyCode) {
        return OrderItem.from(product(sku, stockQty, currencyCode), Quantity.of(quantity));
    }

    private static Customer customer() {
        return new Customer(CustomerId.of(1L), "John Doe");
    }

    private static Order pendingOrder() {
        return Order.create(OrderNumber.of("ORD-2025-001"), customer(),
                List.of(item("LAPTOP-001", 10, 1, "USD")), "tester");
    }

    private static Order confirmedOrder() {
        var order = pendingOrder();
        order.confirm();
        return order;
    }

    private static Order shippedOrder() {
        var order = confirmedOrder();
        order.ship();
        return order;
    }

    private static Order deliveredOrder() {
        var order = shippedOrder();
        order.deliver();
        return order;
    }

    private static Order cancelledOrder() {
        var order = pendingOrder();
        order.cancel("customer changed mind");
        return order;
    }

    // ---- no-arg constructor ----

    @Test
    @DisplayName("Should throw IllegalArgumentException when no-arg constructor is invoked")
    void shouldThrowWhenNoArgConstructorInvoked() throws Exception {
        var constructor = Order.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        var exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        assertEquals("Entity ID cannot be null", exception.getCause().getMessage());
    }

    // ---- create() validation ----

    @Test
    @DisplayName("Should throw IllegalArgumentException when orderNumber is null")
    void shouldThrowWhenOrderNumberIsNull() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> Order.create(null, customer(), List.of(item("LAPTOP-001", 10, 1, "USD")), "tester"));
        assertEquals("Order number cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when customer is null")
    void shouldThrowWhenCustomerIsNull() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> Order.create(OrderNumber.of("ORD-2025-001"), null,
                        List.of(item("LAPTOP-001", 10, 1, "USD")), "tester"));
        assertEquals("Customer cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when items is null")
    void shouldThrowWhenItemsIsNull() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> Order.create(OrderNumber.of("ORD-2025-001"), customer(), null, "tester"));
        assertEquals("Order must have at least one item", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when items is empty")
    void shouldThrowWhenItemsIsEmpty() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> Order.create(OrderNumber.of("ORD-2025-001"), customer(), List.of(), "tester"));
        assertEquals("Order must have at least one item", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when items have mismatched currencies")
    void shouldThrowWhenItemsHaveMismatchedCurrencies() {
        var usdItem = item("LAPTOP-001", 10, 1, "USD");
        var eurItem = item("MOUSE-001", 10, 1, "EUR");
        var exception = assertThrows(IllegalArgumentException.class,
                () -> Order.create(OrderNumber.of("ORD-2025-001"), customer(), List.of(usdItem, eurItem), "tester"));
        assertTrue(exception.getMessage().contains("All items must have the same currency"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Should propagate AuditInfo's exception when createdBy is blank")
    void shouldPropagateAuditInfoExceptionWhenCreatedByIsBlank(String createdBy) {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> Order.create(OrderNumber.of("ORD-2025-001"), customer(),
                        List.of(item("LAPTOP-001", 10, 1, "USD")), createdBy));
        assertEquals("createdBy must not be blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should create Order successfully with PENDING status and correct total")
    void shouldCreateOrderSuccessfully() {
        var firstItem = item("LAPTOP-001", 10, 2, "USD");
        var secondItem = item("MOUSE-001", 10, 1, "USD");
        var order = Order.create(OrderNumber.of("ORD-2025-001"), customer(),
                List.of(firstItem, secondItem), "tester");

        assertTrue(order.getStatus().isPending());
        assertEquals(2, order.getItems().size());
        assertEquals(firstItem.getSubtotal().add(secondItem.getSubtotal()), order.getTotalAmount());
        assertEquals(1, order.getDomainEvents().size());

        var event = (OrderCreated) order.getDomainEvents().get(0);
        assertEquals(customer().customerId(), event.customerId());
        assertEquals(customer().customerName(), event.customerName());
        assertEquals(order.getTotalAmount(), event.totalAmount());
    }

    @Test
    @DisplayName("Order.getId() returns the id assigned at creation")
    void getIdReturnsAssignedId() {
        var order = pendingOrder();
        assertNotNull(order.getId());
        assertEquals(order.getId(), order.getId());
    }

    // ---- confirm() ----

    @Test
    @DisplayName("Should confirm order from PENDING and register OrderConfirmed")
    void shouldConfirmFromPending() {
        var order = pendingOrder();
        order.confirm();
        assertTrue(order.getStatus().isConfirmed());
        assertEquals(2, order.getDomainEvents().size());
        var event = (OrderConfirmed) order.getDomainEvents().get(1);
        assertEquals(order.getId(), event.orderId());
    }

    @Test
    @DisplayName("Should throw IllegalStateException confirming from CONFIRMED")
    void shouldThrowConfirmingFromConfirmed() {
        var order = confirmedOrder();
        var exception = assertThrows(IllegalStateException.class, order::confirm);
        assertEquals("Invalid status transition from CONFIRMED to CONFIRMED", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalStateException confirming from SHIPPED")
    void shouldThrowConfirmingFromShipped() {
        var order = shippedOrder();
        assertThrows(IllegalStateException.class, order::confirm);
    }

    @Test
    @DisplayName("Should throw IllegalStateException confirming from DELIVERED")
    void shouldThrowConfirmingFromDelivered() {
        var order = deliveredOrder();
        assertThrows(IllegalStateException.class, order::confirm);
    }

    @Test
    @DisplayName("Should throw IllegalStateException confirming from CANCELLED")
    void shouldThrowConfirmingFromCancelled() {
        var order = cancelledOrder();
        assertThrows(IllegalStateException.class, order::confirm);
    }

    // ---- ship() ----

    @Test
    @DisplayName("Should ship order from CONFIRMED and register OrderShipped")
    void shouldShipFromConfirmed() {
        var order = confirmedOrder();
        order.ship();
        assertTrue(order.getStatus().isShipped());
        var event = (OrderShipped) order.getDomainEvents().get(2);
        assertEquals(order.getId(), event.orderId());
    }

    @Test
    @DisplayName("Should throw IllegalStateException shipping from PENDING")
    void shouldThrowShippingFromPending() {
        var order = pendingOrder();
        assertThrows(IllegalStateException.class, order::ship);
    }

    @Test
    @DisplayName("Should throw IllegalStateException shipping from SHIPPED")
    void shouldThrowShippingFromShipped() {
        var order = shippedOrder();
        assertThrows(IllegalStateException.class, order::ship);
    }

    @Test
    @DisplayName("Should throw IllegalStateException shipping from DELIVERED")
    void shouldThrowShippingFromDelivered() {
        var order = deliveredOrder();
        assertThrows(IllegalStateException.class, order::ship);
    }

    @Test
    @DisplayName("Should throw IllegalStateException shipping from CANCELLED")
    void shouldThrowShippingFromCancelled() {
        var order = cancelledOrder();
        assertThrows(IllegalStateException.class, order::ship);
    }

    // ---- deliver() ----

    @Test
    @DisplayName("Should deliver order from SHIPPED and register OrderDelivered")
    void shouldDeliverFromShipped() {
        var order = shippedOrder();
        order.deliver();
        assertTrue(order.getStatus().isDelivered());
        var event = (OrderDelivered) order.getDomainEvents().get(3);
        assertEquals(order.getId(), event.orderId());
    }

    @Test
    @DisplayName("Should throw IllegalStateException delivering from PENDING")
    void shouldThrowDeliveringFromPending() {
        var order = pendingOrder();
        assertThrows(IllegalStateException.class, order::deliver);
    }

    @Test
    @DisplayName("Should throw IllegalStateException delivering from CONFIRMED")
    void shouldThrowDeliveringFromConfirmed() {
        var order = confirmedOrder();
        assertThrows(IllegalStateException.class, order::deliver);
    }

    @Test
    @DisplayName("Should throw IllegalStateException delivering from DELIVERED")
    void shouldThrowDeliveringFromDelivered() {
        var order = deliveredOrder();
        assertThrows(IllegalStateException.class, order::deliver);
    }

    @Test
    @DisplayName("Should throw IllegalStateException delivering from CANCELLED")
    void shouldThrowDeliveringFromCancelled() {
        var order = cancelledOrder();
        assertThrows(IllegalStateException.class, order::deliver);
    }

    // ---- cancel() ----

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Should throw IllegalArgumentException when cancellation reason is blank")
    void shouldThrowWhenCancelReasonBlank(String reason) {
        var order = pendingOrder();
        var exception = assertThrows(IllegalArgumentException.class, () -> order.cancel(reason));
        assertEquals("Cancellation reason cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should cancel order from PENDING and register OrderCancelled")
    void shouldCancelFromPending() {
        var order = pendingOrder();
        order.cancel("no longer needed");
        assertTrue(order.getStatus().isCancelled());
        var event = (OrderCancelled) order.getDomainEvents().get(1);
        assertEquals("no longer needed", event.reason());
        assertEquals(order.getId(), event.orderId());
    }

    @Test
    @DisplayName("Should cancel order from CONFIRMED and register OrderCancelled")
    void shouldCancelFromConfirmed() {
        var order = confirmedOrder();
        order.cancel("out of stock");
        assertTrue(order.getStatus().isCancelled());
    }

    @Test
    @DisplayName("Should throw IllegalStateException cancelling from SHIPPED")
    void shouldThrowCancellingFromShipped() {
        var order = shippedOrder();
        assertThrows(IllegalStateException.class, () -> order.cancel("too late"));
    }

    @Test
    @DisplayName("Should throw IllegalStateException cancelling from DELIVERED")
    void shouldThrowCancellingFromDelivered() {
        var order = deliveredOrder();
        assertThrows(IllegalStateException.class, () -> order.cancel("too late"));
    }

    @Test
    @DisplayName("Should throw IllegalStateException cancelling from CANCELLED")
    void shouldThrowCancellingFromCancelled() {
        var order = cancelledOrder();
        assertThrows(IllegalStateException.class, () -> order.cancel("again")); }

    // ---- addItem() ----

    @Test
    @DisplayName("Should throw IllegalStateException adding item when not PENDING")
    void shouldThrowAddingItemWhenNotPending() {
        var order = confirmedOrder();
        var exception = assertThrows(IllegalStateException.class,
                () -> order.addItem(item("MOUSE-001", 10, 1, "USD")));
        assertEquals("Cannot add items to order in status: CONFIRMED", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException adding null item")
    void shouldThrowAddingNullItem() {
        var order = pendingOrder();
        var exception = assertThrows(IllegalArgumentException.class, () -> order.addItem(null));
        assertEquals("Order item cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should add item and recalculate total while PENDING")
    void shouldAddItemAndRecalculateTotal() {
        var order = pendingOrder();
        var newItem = item("MOUSE-001", 10, 2, "USD");
        order.addItem(newItem);

        assertEquals(2, order.getItems().size());
        assertEquals(order.getItems().get(0).getSubtotal().add(newItem.getSubtotal()), order.getTotalAmount());
        assertEquals(1, order.getDomainEvents().size());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when added item has mismatched currency")
    void shouldThrowWhenAddedItemCurrencyMismatches() {
        var order = pendingOrder();
        var eurItem = item("MOUSE-001", 10, 1, "EUR");
        assertThrows(IllegalArgumentException.class, () -> order.addItem(eurItem));
    }

    // ---- removeItem() ----

    @Test
    @DisplayName("Should throw IllegalStateException removing item when not PENDING")
    void shouldThrowRemovingItemWhenNotPending() {
        var order = confirmedOrder();
        var existingItem = order.getItems().get(0);
        var exception = assertThrows(IllegalStateException.class, () -> order.removeItem(existingItem));
        assertEquals("Cannot remove items from order in status: CONFIRMED", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException removing null item")
    void shouldThrowRemovingNullItem() {
        var order = pendingOrder();
        var exception = assertThrows(IllegalArgumentException.class, () -> order.removeItem(null));
        assertEquals("Order item cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException removing item not found in order")
    void shouldThrowRemovingItemNotFound() {
        var order = pendingOrder();
        var foreignItem = item("MOUSE-001", 10, 1, "USD");
        var exception = assertThrows(IllegalArgumentException.class, () -> order.removeItem(foreignItem));
        assertEquals("Item not found in order", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalStateException removing the last remaining item")
    void shouldThrowRemovingLastItem() {
        var order = pendingOrder();
        var onlyItem = order.getItems().get(0);
        var exception = assertThrows(IllegalStateException.class, () -> order.removeItem(onlyItem));
        assertEquals("Order must have at least one item", exception.getMessage());
    }

    @Test
    @DisplayName("Should remove item and recalculate total while PENDING")
    void shouldRemoveItemAndRecalculateTotal() {
        var order = pendingOrder();
        var newItem = item("MOUSE-001", 10, 2, "USD");
        order.addItem(newItem);

        order.removeItem(newItem);

        assertEquals(1, order.getItems().size());
        assertEquals(order.getItems().get(0).getSubtotal(), order.getTotalAmount());
    }

    // ---- getItems() ----

    @Test
    @DisplayName("Should return unmodifiable list from getItems()")
    void shouldReturnUnmodifiableItems() {
        var order = pendingOrder();
        var items = order.getItems();
        assertThrows(UnsupportedOperationException.class, () -> items.add(item("MOUSE-001", 10, 1, "USD")));
    }

    // ---- domain event accumulation ----

    @Test
    @DisplayName("Should accumulate domain events across the full lifecycle and clear on demand")
    void shouldAccumulateAndClearDomainEvents() {
        var order = pendingOrder();
        order.confirm();
        order.ship();
        order.deliver();

        assertEquals(4, order.getDomainEvents().size());
        assertThrows(UnsupportedOperationException.class, () -> order.getDomainEvents().add(null));

        order.clearDomainEvents();
        assertTrue(order.getDomainEvents().isEmpty());
    }

    // ---- unreachable-via-public-API private branches (via reflection) ----

    @Test
    @DisplayName("validateItems(empty list) throws IllegalArgumentException (unreachable via public API)")
    void validateItemsThrowsForEmptyListViaReflection() throws Exception {
        var method = Order.class.getDeclaredMethod("validateItems", List.class);
        method.setAccessible(true);
        var exception = assertThrows(InvocationTargetException.class, () -> method.invoke(null, List.of()));
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        assertEquals("Order items cannot be empty", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("calculateTotal(empty list) throws IllegalArgumentException (unreachable via public API)")
    void calculateTotalThrowsForEmptyListViaReflection() throws Exception {
        var method = Order.class.getDeclaredMethod("calculateTotal", List.class);
        method.setAccessible(true);
        var exception = assertThrows(InvocationTargetException.class, () -> method.invoke(null, List.of()));
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        assertEquals("Cannot calculate total for empty order", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("validateItems(items) throws IllegalArgumentException when a subtotal is tampered (unreachable via normal construction)")
    void validateItemsThrowsForSubtotalMismatchViaReflection() throws Exception {
        var product = product("LAPTOP-001", 10, "USD");

        var itemConstructor = OrderItem.class.getDeclaredConstructor(
                OrderItemId.class,
                ProductId.class,
                String.class,
                Quantity.class,
                Money.class,
                Money.class);
        itemConstructor.setAccessible(true);
        var tamperedItem = itemConstructor.newInstance(
                OrderItemId.generate(),
                product.getId(),
                product.getName().value(),
                Quantity.of(1),
                product.getPrice(),
                Money.of(BigDecimal.valueOf(999), Currency.getInstance("USD")));

        var method = Order.class.getDeclaredMethod("validateItems", List.class);
        method.setAccessible(true);
        var exception = assertThrows(InvocationTargetException.class,
                () -> method.invoke(null, List.of(tamperedItem)));
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("Item subtotal mismatch"));
    }
}