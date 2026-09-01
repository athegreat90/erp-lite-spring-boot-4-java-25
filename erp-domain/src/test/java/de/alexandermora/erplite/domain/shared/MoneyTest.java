package de.alexandermora.erplite.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Money Domain Test")
class MoneyTest {

    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency EUR = Currency.getInstance("EUR");

    @Test
    @DisplayName("Should throw NullPointerException when amount is null")
    void shouldThrowWhenAmountIsNull() {
        assertThrows(NullPointerException.class, () -> new Money(null, USD));
    }

    @Test
    @DisplayName("Should throw NullPointerException when currency is null")
    void shouldThrowWhenCurrencyIsNull() {
        assertThrows(NullPointerException.class, () -> new Money(BigDecimal.TEN, null));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when amount is negative")
    void shouldThrowWhenAmountIsNegative() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> new Money(BigDecimal.valueOf(-1), USD));
        assertEquals("amount must be >= 0", exception.getMessage());
    }

    @Test
    @DisplayName("Should create Money with zero amount as a valid boundary")
    void shouldAllowZeroAmount() {
        var money = new Money(BigDecimal.ZERO, USD);
        assertEquals(BigDecimal.ZERO, money.amount());
    }

    @Test
    @DisplayName("Should create Money via of(BigDecimal, Currency)")
    void shouldCreateViaOfBigDecimal() {
        var money = Money.of(BigDecimal.TEN, USD);
        assertEquals(BigDecimal.TEN, money.amount());
        assertEquals(USD, money.currency());
    }

    @Test
    @DisplayName("Should create Money via of(double, Currency)")
    void shouldCreateViaOfDouble() {
        var money = Money.of(10.5, USD);
        assertEquals(0, BigDecimal.valueOf(10.5).compareTo(money.amount()));
    }

    @Test
    @DisplayName("Should add two Money values with the same currency")
    void shouldAddSameCurrency() {
        var result = Money.of(10, USD).add(Money.of(5, USD));
        assertEquals(0, BigDecimal.valueOf(15).compareTo(result.amount()));
    }

    @Test
    @DisplayName("Should throw when adding Money with a different currency")
    void shouldThrowWhenAddingDifferentCurrency() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> Money.of(10, USD).add(Money.of(5, EUR)));
        assertTrue(exception.getMessage().startsWith("Currency mismatch"));
    }

    @Test
    @DisplayName("Should throw NullPointerException when adding null")
    void shouldThrowWhenAddingNull() {
        assertThrows(NullPointerException.class, () -> Money.of(10, USD).add(null));
    }

    @Test
    @DisplayName("Should subtract two Money values with the same currency")
    void shouldSubtractSameCurrency() {
        var result = Money.of(10, USD).subtract(Money.of(4, USD));
        assertEquals(0, BigDecimal.valueOf(6).compareTo(result.amount()));
    }

    @Test
    @DisplayName("Should allow subtraction resulting in exactly zero")
    void shouldAllowSubtractionResultingInZero() {
        var result = Money.of(10, USD).subtract(Money.of(10, USD));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.amount()));
    }

    @Test
    @DisplayName("Should throw when subtraction results in a negative amount")
    void shouldThrowWhenSubtractionIsNegative() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> Money.of(5, USD).subtract(Money.of(10, USD)));
        assertEquals("Result of subtraction cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw when subtracting Money with a different currency")
    void shouldThrowWhenSubtractingDifferentCurrency() {
        assertThrows(IllegalArgumentException.class, () -> Money.of(10, USD).subtract(Money.of(5, EUR)));
    }

    @Test
    @DisplayName("Should throw NullPointerException when subtracting null")
    void shouldThrowWhenSubtractingNull() {
        assertThrows(NullPointerException.class, () -> Money.of(10, USD).subtract(null));
    }

    @Test
    @DisplayName("Should throw when multiplying by a negative multiplier")
    void shouldThrowWhenMultiplierIsNegative() {
        var exception = assertThrows(IllegalArgumentException.class, () -> Money.of(10, USD).multiply(-1));
        assertEquals("Multiplier must be >= 0", exception.getMessage());
    }

    @Test
    @DisplayName("Should allow multiplying by zero as a valid boundary")
    void shouldAllowMultiplyByZero() {
        var result = Money.of(10, USD).multiply(0);
        assertEquals(0, BigDecimal.ZERO.compareTo(result.amount()));
    }

    @Test
    @DisplayName("Should multiply by a positive int")
    void shouldMultiplyByPositiveInt() {
        var result = Money.of(10, USD).multiply(3);
        assertEquals(0, BigDecimal.valueOf(30).compareTo(result.amount()));
    }

    @Test
    @DisplayName("Should throw NullPointerException when multiplying by a null Quantity")
    void shouldThrowWhenMultiplyingByNullQuantity() {
        assertThrows(NullPointerException.class, () -> Money.of(10, USD).multiply((Quantity) null));
    }

    @Test
    @DisplayName("Should multiply by a Quantity")
    void shouldMultiplyByQuantity() {
        var result = Money.of(10, USD).multiply(Quantity.of(4));
        assertEquals(0, BigDecimal.valueOf(40).compareTo(result.amount()));
    }

    @Test
    @DisplayName("Should be equal and share hashCode when amount and currency match")
    void shouldBeEqualWhenFieldsMatch() {
        var money1 = Money.of(BigDecimal.TEN, USD);
        var money2 = Money.of(BigDecimal.TEN, USD);
        assertEquals(money1, money2);
        assertEquals(money1.hashCode(), money2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when currency differs")
    void shouldNotBeEqualWhenCurrencyDiffers() {
        var money1 = Money.of(BigDecimal.TEN, USD);
        var money2 = Money.of(BigDecimal.TEN, EUR);
        assertNotEquals(money1, money2);
    }

    @Test
    @DisplayName("Should include amount and currency in toString")
    void shouldIncludeFieldsInToString() {
        var result = Money.of(BigDecimal.TEN, USD).toString();
        assertTrue(result.contains("10"));
        assertTrue(result.contains("USD"));
    }
}