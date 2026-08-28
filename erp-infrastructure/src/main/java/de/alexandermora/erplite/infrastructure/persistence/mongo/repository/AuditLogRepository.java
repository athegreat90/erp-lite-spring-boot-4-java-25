package de.alexandermora.erplite.infrastructure.persistence.mongo.repository;

import de.alexandermora.erplite.infrastructure.persistence.mongo.document.AuditLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLogDocument, String> {

    List<AuditLogDocument> findByClassNameAndMethodName(String className, String methodName);

    List<AuditLogDocument> findByUserIdOrderByTimestampDesc(String userId);

    List<AuditLogDocument> findBySuccess(boolean success);
}