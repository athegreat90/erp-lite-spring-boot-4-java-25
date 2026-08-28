package de.alexandermora.erplite.infrastructure.persistence.jpa.repository;

import de.alexandermora.erplite.infrastructure.persistence.jpa.entity.OrderEntity;
import de.alexandermora.erplite.infrastructure.persistence.jpa.entity.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    Optional<OrderEntity> findByOrderNumber(String orderNumber);

    boolean existsByOrderNumber(String orderNumber);

    List<OrderEntity> findByStatus(OrderStatus status);

    @EntityGraph(attributePaths = "items")
    Optional<OrderEntity> findWithItemsById(UUID id);
}