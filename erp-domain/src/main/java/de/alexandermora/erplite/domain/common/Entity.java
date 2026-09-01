package de.alexandermora.erplite.domain.common;


import lombok.Getter;

import java.util.Objects;

/**
 * Base class for all domain entities.
 * Entities are identified by their ID, not by their attributes.
 *
 * @param <ID> the type of the entity identifier
 */
@Getter
public abstract class Entity<ID> {

    protected final ID id;

    protected Entity(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("Entity ID cannot be null");
        }
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}