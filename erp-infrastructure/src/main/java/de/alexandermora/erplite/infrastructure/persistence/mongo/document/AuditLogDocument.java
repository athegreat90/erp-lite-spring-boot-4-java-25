package de.alexandermora.erplite.infrastructure.persistence.mongo.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Maps the {@code audit_logs} collection in {@code erp_catalog_db}: an execution trail of
 * application use cases. The Mongo {@code ObjectId} {@code _id} is exposed as its hex
 * {@code String}. Indexes are owned by {@code db/mongodb/init/init-mongo.js}.
 */
@Document(collection = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AuditLogDocument {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String className;

    private String methodName;

    private String userId;

    private Instant timestamp;

    private long executionTimeMs;

    private boolean success;

    private String errorMessage;

    private String ipAddress;

    private String endpoint;
}