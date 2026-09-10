package de.alexandermora.erplite.infrastructure.persistence.jpa.adapter;

import de.alexandermora.erplite.domain.entity.order.OrderId;
import de.alexandermora.erplite.domain.entity.order.OrderNumber;
import de.alexandermora.erplite.domain.entity.order.OrderRoot;
import de.alexandermora.erplite.domain.port.repository.OrderRepositoryPort;
import de.alexandermora.erplite.domain.shared.CustomerId;
import de.alexandermora.erplite.infrastructure.persistence.jpa.entity.OrderEntity;
import de.alexandermora.erplite.infrastructure.persistence.jpa.mapper.OrderJpaMapper;
import de.alexandermora.erplite.infrastructure.persistence.jpa.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

@Repository
@Slf4j
@AllArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderRepository orderRepository;
    private final OrderJpaMapper orderJpaMapper;


    @Override
    public OrderRoot save(OrderRoot order) {
        log.debug("Saving order: {}", order);
        try {
            var orderEntity = orderJpaMapper.toEntity(order);
            orderEntity.setId(order.getId().value());
            zipOrderItemsIds(order, orderEntity);
            var savedOrderEntity = orderRepository.save(orderEntity);
            log.debug("Order saved successfully: {}", savedOrderEntity);
            return orderJpaMapper.toDomain(savedOrderEntity);
        } catch (Exception e) {
            log.error("Error saving order: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Error saving order", e);
        }
    }

    @Override
    public Optional<OrderRoot> findById(OrderId id) {
        try {
            var orderEntityOptional = orderRepository.findById(id.value());
            if (orderEntityOptional.isEmpty()) {
                log.debug("Order with id {} not found", id);
                return Optional.empty();
            }
            return orderEntityOptional.map(orderJpaMapper::toDomain);
        } catch (Exception e) {
            log.error("Error finding order by id {}: {}", id, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<OrderRoot> findAllByOrderNumber(OrderNumber orderNumber) {
        try {
            var orderEntityOptional = orderRepository.findByOrderNumber(orderNumber.value());
            if (orderEntityOptional.isEmpty()) {
                log.debug("Order with order number {} not found", orderNumber);
                return Optional.empty();
            }
            return orderEntityOptional.map(orderJpaMapper::toDomain);
        } catch (Exception e) {
            log.error("Error finding order by order number {}: {}", orderNumber, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public List<OrderRoot> findOrderByCustomerId(CustomerId customerId) {
        try {
            var orderEntities = orderRepository.findAllByCustomerId(customerId.value());
            return orderEntities.stream().map(orderJpaMapper::toDomain).filter(Objects::nonNull).toList();
        } catch (Exception e) {
            log.error("Error finding orders by customer id {}: {}", customerId, e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public void delete(OrderRoot order) {
        log.info("Deleting order: {}", order);
        try {
            orderRepository.deleteById(order.getId().value());
            log.info("Order deleted successfully: {}", order);
        } catch (Exception e) {
            log.error("Error deleting order {}: {}", order.getId().value(), e.getMessage(), e);
            throw new IllegalArgumentException("Error deleting order", e);
        }
    }

    private void zipOrderItemsIds(OrderRoot root, OrderEntity entity) {
        if (root.getItems() == null || root.getItems().isEmpty()) {
            log.debug("Items are null or empty, skipping zipOrderItemsIds");
            return;
        }

        var itemsEntity = entity.getItems();
        var itemsDomain = root.getItems();

        IntStream.range(0, itemsDomain.size()).forEach(i ->
                itemsEntity.get(i).setId(itemsDomain.get(i).getId().value()));

    }
}
