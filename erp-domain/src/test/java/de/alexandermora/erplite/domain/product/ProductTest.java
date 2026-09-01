package de.alexandermora.erplite.domain.product;

import de.alexandermora.erplite.domain.product.events.ProductCreated;
import de.alexandermora.erplite.domain.product.events.ProductDeactivated;
import de.alexandermora.erplite.domain.product.events.ProductUpdated;
import de.alexandermora.erplite.domain.product.events.StockChanged;
import de.alexandermora.erplite.domain.shared.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product Domain Test")
class ProductTest {

    private static final Currency USD = Currency.getInstance("USD");

    private SKU sku() {
        return SKU.of("LAPTOP-001");
    }

    private ProductName name() {
        return ProductName.of("Laptop");
    }

    private Money price() {
        return Money.of(new BigDecimal("999.99"), USD);
    }

    private Stock stock() {
        return Stock.of(10);
    }

    private CategoryReference category() {
        return CategoryReference.of("cat-electronics");
    }

    private ProductImage image() {
        return ProductImage.of("https://example.com/images/laptop.png");
    }

    private Product createProduct() {
        return Product.create(sku(), name(), "A laptop", price(), stock(), category(), image(), "alex");
    }

    @Test
    @DisplayName("Should create a Product with all fields and register a ProductCreated event")
    void shouldCreateProductAndRegisterEvent() {
        var product = createProduct();

        assertNotNull(product.getId());
        assertEquals(sku(), product.getSku());
        assertEquals(name(), product.getName());
        assertEquals("A laptop", product.getDescription());
        assertEquals(price(), product.getPrice());
        assertEquals(stock(), product.getStock());
        assertEquals(category(), product.getCategory());
        assertEquals(image(), product.getImage());
        assertTrue(product.isActive());
        assertEquals("alex", product.getAuditInfo().createdBy());

        assertEquals(1, product.getDomainEvents().size());
        var event = product.getDomainEvents().get(0);
        assertInstanceOf(ProductCreated.class, event);
        var created = (ProductCreated) event;
        assertEquals(product.getId(), created.productId());
        assertEquals(sku(), created.sku());
        assertEquals(name(), created.name());
        assertEquals(price(), created.price());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when creating with a null price")
    void shouldThrowWhenCreatingWithNullPrice() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> Product.create(sku(), name(), "A laptop", null, stock(), category(), image(), "alex"));
        assertEquals("Price cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when creating with a zero price")
    void shouldThrowWhenCreatingWithZeroPrice() {
        var zeroPrice = Money.of(BigDecimal.ZERO, USD);
        var exception = assertThrows(IllegalArgumentException.class,
                () -> Product.create(sku(), name(), "A laptop", zeroPrice, stock(), category(), image(), "alex"));
        assertEquals("Price must be greater than 0", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"   "})
    @DisplayName("Should propagate AuditInfo's exception when createdBy is null or blank")
    void shouldPropagateAuditInfoExceptionWhenCreatedByIsBlank(String createdBy) {
        assertThrows(IllegalArgumentException.class,
                () -> Product.create(sku(), name(), "A laptop", price(), stock(), category(), image(), createdBy));
    }

    @Test
    @DisplayName("Should update product fields and register a ProductUpdated event")
    void shouldUpdateProductFieldsAndRegisterEvent() {
        var product = createProduct();
        var newName = ProductName.of("Gaming Laptop");
        var newPrice = Money.of(new BigDecimal("1299.99"), USD);
        var newCategory = CategoryReference.of("cat-gaming");
        var newImage = ProductImage.of("https://example.com/images/gaming-laptop.png");

        product.update(newName, "A gaming laptop", newPrice, newCategory, newImage);

        assertEquals(newName, product.getName());
        assertEquals("A gaming laptop", product.getDescription());
        assertEquals(newPrice, product.getPrice());
        assertEquals(newCategory, product.getCategory());
        assertEquals(newImage, product.getImage());

        assertEquals(2, product.getDomainEvents().size());
        assertInstanceOf(ProductUpdated.class, product.getDomainEvents().get(1));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when updating with a null price")
    void shouldThrowWhenUpdatingWithNullPrice() {
        var product = createProduct();
        var exception = assertThrows(IllegalArgumentException.class,
                () -> product.update(name(), "A laptop", null, category(), image()));
        assertEquals("Price cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when updating with a zero price")
    void shouldThrowWhenUpdatingWithZeroPrice() {
        var product = createProduct();
        var zeroPrice = Money.of(BigDecimal.ZERO, USD);
        var exception = assertThrows(IllegalArgumentException.class,
                () -> product.update(name(), "A laptop", zeroPrice, category(), image()));
        assertEquals("Price must be greater than 0", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"   "})
    @DisplayName("Should throw IllegalArgumentException when incrementStock reason is null or blank")
    void shouldThrowWhenIncrementStockReasonIsBlank(String reason) {
        var product = createProduct();
        var exception = assertThrows(IllegalArgumentException.class, () -> product.incrementStock(5, reason));
        assertEquals("Reason for stock increment cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should propagate Stock's exception when incrementing by a negative quantity")
    void shouldPropagateStockExceptionWhenIncrementingByNegativeQuantity() {
        var product = createProduct();
        assertThrows(IllegalArgumentException.class, () -> product.incrementStock(-1, "restock"));
    }

    @Test
    @DisplayName("Should increment stock and register a StockChanged event")
    void shouldIncrementStockAndRegisterEvent() {
        var product = createProduct();
        product.incrementStock(5, "restock");

        assertEquals(15, product.getStock().value());
        assertEquals(2, product.getDomainEvents().size());
        var event = (StockChanged) product.getDomainEvents().get(1);
        assertEquals(10, event.oldStock());
        assertEquals(15, event.newStock());
        assertEquals("restock", event.reason());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"   "})
    @DisplayName("Should throw IllegalArgumentException when decrementStock reason is null or blank")
    void shouldThrowWhenDecrementStockReasonIsBlank(String reason) {
        var product = createProduct();
        var exception = assertThrows(IllegalArgumentException.class, () -> product.decrementStock(5, reason));
        assertEquals("Reason for stock decrement cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should propagate Stock's exception when decrementing by a negative quantity")
    void shouldPropagateStockExceptionWhenDecrementingByNegativeQuantity() {
        var product = createProduct();
        assertThrows(IllegalArgumentException.class, () -> product.decrementStock(-1, "sale"));
    }

    @Test
    @DisplayName("Should propagate Stock's exception when decrementing beyond available stock")
    void shouldPropagateStockExceptionWhenDecrementingBeyondAvailableStock() {
        var product = createProduct();
        var exception = assertThrows(IllegalArgumentException.class, () -> product.decrementStock(11, "sale"));
        assertEquals("Insufficient stock", exception.getMessage());
    }

    @Test
    @DisplayName("Should decrement stock down to exactly zero and register a StockChanged event")
    void shouldDecrementStockToExactlyZero() {
        var product = createProduct();
        product.decrementStock(10, "sale");

        assertEquals(0, product.getStock().value());
        var event = (StockChanged) product.getDomainEvents().get(1);
        assertEquals(10, event.oldStock());
        assertEquals(0, event.newStock());
        assertEquals("sale", event.reason());
    }

    @Test
    @DisplayName("Should change price and register a ProductUpdated event")
    void shouldChangePriceAndRegisterEvent() {
        var product = createProduct();
        var newPrice = Money.of(new BigDecimal("799.99"), USD);
        product.changePrice(newPrice);

        assertEquals(newPrice, product.getPrice());
        assertEquals(2, product.getDomainEvents().size());
        assertInstanceOf(ProductUpdated.class, product.getDomainEvents().get(1));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when changing to a null price")
    void shouldThrowWhenChangingToNullPrice() {
        var product = createProduct();
        var exception = assertThrows(IllegalArgumentException.class, () -> product.changePrice(null));
        assertEquals("Price cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when changing to a zero price")
    void shouldThrowWhenChangingToZeroPrice() {
        var product = createProduct();
        var zeroPrice = Money.of(BigDecimal.ZERO, USD);
        var exception = assertThrows(IllegalArgumentException.class, () -> product.changePrice(zeroPrice));
        assertEquals("Price must be greater than 0", exception.getMessage());
    }

    @Test
    @DisplayName("Should deactivate an active product and register a ProductDeactivated event")
    void shouldDeactivateActiveProduct() {
        var product = createProduct();
        product.deactivate();

        assertFalse(product.isActive());
        assertEquals(2, product.getDomainEvents().size());
        assertInstanceOf(ProductDeactivated.class, product.getDomainEvents().get(1));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when deactivating an already inactive product")
    void shouldThrowWhenDeactivatingAlreadyInactiveProduct() {
        var product = createProduct();
        product.deactivate();

        var exception = assertThrows(IllegalStateException.class, product::deactivate);
        assertEquals("Product is already deactivated", exception.getMessage());
    }

    @Test
    @DisplayName("Should activate an inactive product and register a ProductUpdated event (current behavior)")
    void shouldActivateInactiveProductAndRegisterProductUpdated() {
        var product = createProduct();
        product.deactivate();

        product.activate();

        assertTrue(product.isActive());
        assertEquals(3, product.getDomainEvents().size());
        assertInstanceOf(ProductUpdated.class, product.getDomainEvents().get(2));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when activating an already active product")
    void shouldThrowWhenActivatingAlreadyActiveProduct() {
        var product = createProduct();
        var exception = assertThrows(IllegalStateException.class, product::activate);
        assertEquals("Product is already active", exception.getMessage());
    }

    @Test
    @DisplayName("Should report available stock when active and stock is sufficient")
    void shouldReportAvailableStockWhenActiveAndSufficient() {
        var product = createProduct();
        assertTrue(product.hasAvailableStock(5));
    }

    @Test
    @DisplayName("Should report available stock at the exact boundary")
    void shouldReportAvailableStockAtExactBoundary() {
        var product = createProduct();
        assertTrue(product.hasAvailableStock(10));
    }

    @Test
    @DisplayName("Should report unavailable stock when active but stock is insufficient")
    void shouldReportUnavailableStockWhenActiveButInsufficient() {
        var product = createProduct();
        assertFalse(product.hasAvailableStock(11));
    }

    @Test
    @DisplayName("Should report unavailable stock when inactive even if stock would be sufficient")
    void shouldReportUnavailableStockWhenInactive() {
        var product = createProduct();
        product.deactivate();
        assertFalse(product.hasAvailableStock(1));
    }

    @Test
    @DisplayName("Should accumulate domain events across multiple calls and clear them on demand")
    void shouldAccumulateAndClearDomainEvents() {
        var product = createProduct();
        product.incrementStock(1, "restock");
        product.deactivate();

        assertEquals(3, product.getDomainEvents().size());

        product.clearDomainEvents();

        assertTrue(product.getDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("Should return an unmodifiable list from getDomainEvents")
    void shouldReturnUnmodifiableDomainEventsList() {
        var product = createProduct();
        var events = product.getDomainEvents();
        assertThrows(UnsupportedOperationException.class, () -> events.add(null));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when invoking the no-arg constructor via reflection")
    void shouldThrowWhenInvokingNoArgConstructorViaReflection() throws NoSuchMethodException {
        Constructor<Product> constructor = Product.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        var invocationException = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(IllegalArgumentException.class, invocationException.getCause());
        assertEquals("Entity ID cannot be null", invocationException.getCause().getMessage());
    }
}