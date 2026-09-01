package de.alexandermora.erplite.domain.shared;

import java.time.Instant;
import java.util.Objects;

/**
 * Audit information for aggregates.
 */
public record AuditInfo(String createdBy, Instant createdAt, Instant updatedAt) {

    public AuditInfo {
        if (createdBy == null || createdBy.isBlank()) {
            throw new IllegalArgumentException("createdBy must not be blank");
        }
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public static AuditInfo create(String createdBy, Instant timestamp) {
        return new AuditInfo(createdBy, timestamp, timestamp);
    }

    public AuditInfo updateTimestamp() {
        return new AuditInfo(this.createdBy, this.createdAt, Instant.now());
    }
}