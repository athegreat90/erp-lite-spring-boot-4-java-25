package de.alexandermora.erplite.domain.order;

import de.alexandermora.erplite.domain.shared.CustomerId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Customer Domain Test")
class CustomerTest {

    @Test
    @DisplayName("Should throw NullPointerException when customerId is null")
    void shouldThrowNullPointerExceptionWhenCustomerIdIsNull() {
        assertThrows(NullPointerException.class, () -> new Customer(null, "John Doe"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Should throw IllegalArgumentException when customerName is null or blank")
    void shouldThrowIllegalArgumentExceptionWhenCustomerNameIsBlank(String name) {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> new Customer(CustomerId.of(1L), name));
        assertEquals("customerName must not be blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should create Customer successfully")
    void shouldCreateCustomerSuccessfully() {
        var customerId = CustomerId.of(1L);
        var customer = new Customer(customerId, "John Doe");
        assertEquals(customerId, customer.customerId());
        assertEquals("John Doe", customer.customerName());
    }

    @Test
    @DisplayName("Should create Customer via of()")
    void shouldCreateCustomerViaOf() {
        var customerId = CustomerId.of(2L);
        var customer = Customer.of(customerId, "Jane Doe");
        assertEquals(customerId, customer.customerId());
        assertEquals("Jane Doe", customer.customerName());
    }

    @Test
    @DisplayName("Should honor equals/hashCode/toString contract")
    void shouldHonorEqualsHashCodeToString() {
        var first = new Customer(CustomerId.of(3L), "Same");
        var second = new Customer(CustomerId.of(3L), "Same");
        var different = new Customer(CustomerId.of(4L), "Other");
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, different);
        assertTrue(first.toString().contains("Same"));
    }
}