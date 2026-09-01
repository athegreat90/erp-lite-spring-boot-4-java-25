package de.alexandermora.erplite.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderStatus Domain Test")
class OrderStatusTest {

    private static final List<String> ALL_STATUSES = List.of(
            OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.SHIPPED,
            OrderStatus.DELIVERED, OrderStatus.CANCELLED
    );

    private static final Map<String, Set<String>> LEGAL_TRANSITIONS = Map.of(
            OrderStatus.PENDING, Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED, Set.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
            OrderStatus.SHIPPED, Set.of(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED, Set.of(),
            OrderStatus.CANCELLED, Set.of()
    );

    @Test
    @DisplayName("Should throw NullPointerException when value is null")
    void shouldThrowNullPointerExceptionWhenValueIsNull() {
        assertThrows(NullPointerException.class, () -> new OrderStatus(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"BOGUS", "", "pending", "Pending"})
    @DisplayName("Should throw IllegalArgumentException for invalid status values")
    void shouldThrowIllegalArgumentExceptionForInvalidValues(String value) {
        assertThrows(IllegalArgumentException.class, () -> new OrderStatus(value));
    }

    @Test
    @DisplayName("Should create OrderStatus via of() and named factories")
    void shouldCreateViaFactories() {
        assertEquals(OrderStatus.PENDING, OrderStatus.of(OrderStatus.PENDING).value());
        assertEquals(OrderStatus.pending(), new OrderStatus(OrderStatus.PENDING));
        assertEquals(OrderStatus.confirmed(), new OrderStatus(OrderStatus.CONFIRMED));
        assertEquals(OrderStatus.shipped(), new OrderStatus(OrderStatus.SHIPPED));
        assertEquals(OrderStatus.delivered(), new OrderStatus(OrderStatus.DELIVERED));
        assertEquals(OrderStatus.cancelled(), new OrderStatus(OrderStatus.CANCELLED));
    }

    @Test
    @DisplayName("Should throw NullPointerException when canTransitionTo is called with null")
    void shouldThrowNullPointerExceptionWhenCanTransitionToNull() {
        assertThrows(NullPointerException.class, () -> OrderStatus.pending().canTransitionTo(null));
    }

    static Stream<Arguments> allStatusPairs() {
        return ALL_STATUSES.stream()
                .flatMap(from -> ALL_STATUSES.stream().map(to -> Arguments.of(from, to)));
    }

    @ParameterizedTest
    @MethodSource("allStatusPairs")
    @DisplayName("Should correctly report legal/illegal transitions for every status pair")
    void shouldReportTransitionLegality(String from, String to) {
        var fromStatus = new OrderStatus(from);
        var toStatus = new OrderStatus(to);
        boolean expected = LEGAL_TRANSITIONS.get(from).contains(to);
        assertEquals(expected, fromStatus.canTransitionTo(toStatus));
    }

    @Test
    @DisplayName("Should report isPending correctly")
    void shouldReportIsPending() {
        assertTrue(OrderStatus.pending().isPending());
        assertFalse(OrderStatus.confirmed().isPending());
    }

    @Test
    @DisplayName("Should report isConfirmed correctly")
    void shouldReportIsConfirmed() {
        assertTrue(OrderStatus.confirmed().isConfirmed());
        assertFalse(OrderStatus.pending().isConfirmed());
    }

    @Test
    @DisplayName("Should report isShipped correctly")
    void shouldReportIsShipped() {
        assertTrue(OrderStatus.shipped().isShipped());
        assertFalse(OrderStatus.pending().isShipped());
    }

    @Test
    @DisplayName("Should report isDelivered correctly")
    void shouldReportIsDelivered() {
        assertTrue(OrderStatus.delivered().isDelivered());
        assertFalse(OrderStatus.pending().isDelivered());
    }

    @Test
    @DisplayName("Should report isCancelled correctly")
    void shouldReportIsCancelled() {
        assertTrue(OrderStatus.cancelled().isCancelled());
        assertFalse(OrderStatus.pending().isCancelled());
    }

    @Test
    @DisplayName("Should report isFinalState true for DELIVERED")
    void shouldReportFinalStateForDelivered() {
        assertTrue(OrderStatus.delivered().isFinalState());
    }

    @Test
    @DisplayName("Should report isFinalState true for CANCELLED")
    void shouldReportFinalStateForCancelled() {
        assertTrue(OrderStatus.cancelled().isFinalState());
    }

    @ParameterizedTest
    @ValueSource(strings = {OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.SHIPPED})
    @DisplayName("Should report isFinalState false for non-final states")
    void shouldReportNonFinalState(String value) {
        assertFalse(new OrderStatus(value).isFinalState());
    }
}