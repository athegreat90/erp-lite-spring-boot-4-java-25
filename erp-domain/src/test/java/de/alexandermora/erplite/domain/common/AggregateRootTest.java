package de.alexandermora.erplite.domain.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AggregateRoot Domain Test")
class AggregateRootTest {

    private record TestEvent(String name) implements DomainEvent {
    }

    private static class TestAggregateRoot extends AggregateRoot<String> {
        TestAggregateRoot(String id) {
            super(id);
        }

        void raise(DomainEvent event) {
            registerEvent(event);
        }
    }

    @Test
    @DisplayName("Should have no domain events initially")
    void shouldHaveNoDomainEventsInitially() {
        var aggregate = new TestAggregateRoot("1");
        assertTrue(aggregate.getDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("Should ignore null events when registering")
    void shouldIgnoreNullEvent() {
        var aggregate = new TestAggregateRoot("1");
        aggregate.raise(null);
        assertTrue(aggregate.getDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("Should register events in insertion order")
    void shouldRegisterEventsInOrder() {
        var aggregate = new TestAggregateRoot("1");
        var event1 = new TestEvent("first");
        var event2 = new TestEvent("second");

        aggregate.raise(event1);
        aggregate.raise(event2);

        assertEquals(2, aggregate.getDomainEvents().size());
        assertEquals(event1, aggregate.getDomainEvents().get(0));
        assertEquals(event2, aggregate.getDomainEvents().get(1));
    }

    @Test
    @DisplayName("Should return an unmodifiable list of domain events")
    void shouldReturnUnmodifiableList() {
        var aggregate = new TestAggregateRoot("1");
        aggregate.raise(new TestEvent("first"));

        var events = aggregate.getDomainEvents();
        assertThrows(UnsupportedOperationException.class, () -> events.add(new TestEvent("second")));
    }

    @Test
    @DisplayName("Should clear domain events")
    void shouldClearDomainEvents() {
        var aggregate = new TestAggregateRoot("1");
        aggregate.raise(new TestEvent("first"));
        aggregate.raise(new TestEvent("second"));

        aggregate.clearDomainEvents();

        assertTrue(aggregate.getDomainEvents().isEmpty());
    }
}