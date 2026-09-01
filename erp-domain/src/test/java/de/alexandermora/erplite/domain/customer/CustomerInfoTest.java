package de.alexandermora.erplite.domain.customer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CustomerInfo Domain Test")
class CustomerInfoTest {

    @Test
    @DisplayName("Should throw IllegalArgumentException when id is null")
    void shouldThrowWhenIdIsNull() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> new CustomerInfo(null, "name", null, null, null, null, null, null));
        assertEquals("Customer ID must be a positive number", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    @DisplayName("Should throw IllegalArgumentException when id is not positive")
    void shouldThrowWhenIdIsNotPositive(long id) {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> new CustomerInfo(id, "name", null, null, null, null, null, null));
        assertEquals("Customer ID must be a positive number", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Should throw IllegalArgumentException when name is null, empty or blank")
    void shouldThrowWhenNameIsBlank(String name) {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> new CustomerInfo(1L, name, null, null, null, null, null, null));
        assertEquals("Customer name must not be blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should create CustomerInfo with optional fields left null")
    void shouldCreateWithOptionalFieldsNull() {
        var customer = new CustomerInfo(1L, "name", null, null, null, null, null, null);
        assertEquals(1L, customer.id());
        assertEquals("name", customer.name());
        assertNull(customer.email());
        assertNull(customer.phone());
        assertNull(customer.address());
        assertNull(customer.city());
        assertNull(customer.zipCode());
        assertNull(customer.companyName());
    }

    @Test
    @DisplayName("Should create CustomerInfo with all fields populated")
    void shouldCreateWithAllFieldsPopulated() {
        var customer = new CustomerInfo(1L, "name", "email@example.com", "phone", "address", "city", "zip", "company");
        assertEquals("email@example.com", customer.email());
        assertEquals("phone", customer.phone());
        assertEquals("address", customer.address());
        assertEquals("city", customer.city());
        assertEquals("zip", customer.zipCode());
        assertEquals("company", customer.companyName());
    }

    @Test
    @DisplayName("Should be equal and share hashCode when all fields match")
    void shouldBeEqualWhenFieldsMatch() {
        var customer1 = new CustomerInfo(1L, "name", null, null, null, null, null, null);
        var customer2 = new CustomerInfo(1L, "name", null, null, null, null, null, null);
        assertEquals(customer1, customer2);
        assertEquals(customer1.hashCode(), customer2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when name differs")
    void shouldNotBeEqualWhenNameDiffers() {
        var customer1 = new CustomerInfo(1L, "name1", null, null, null, null, null, null);
        var customer2 = new CustomerInfo(1L, "name2", null, null, null, null, null, null);
        assertNotEquals(customer1, customer2);
    }

    @Test
    @DisplayName("Should include name in toString")
    void shouldIncludeNameInToString() {
        var customer = new CustomerInfo(1L, "name", null, null, null, null, null, null);
        assertTrue(customer.toString().contains("name"));
    }
}