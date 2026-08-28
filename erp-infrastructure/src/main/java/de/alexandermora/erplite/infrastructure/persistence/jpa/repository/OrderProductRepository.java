package de.alexandermora.erplite.infrastructure.persistence.jpa.repository;

import de.alexandermora.erplite.infrastructure.persistence.jpa.entity.OrderProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProductEntity, UUID> {

    List<OrderProductEntity> findByOrderId(UUID orderId);
}