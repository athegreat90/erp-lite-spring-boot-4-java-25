package de.alexandermora.erplite.domain.port.repository;

import de.alexandermora.erplite.domain.entity.order.OrderId;
import de.alexandermora.erplite.domain.entity.order.OrderNumber;
import de.alexandermora.erplite.domain.entity.order.OrderRoot;
import de.alexandermora.erplite.domain.shared.CustomerId;

import java.util.List;
import java.util.Optional;


/*
* Port for storage or consult Orders.
* */
public interface OrderRepositoryPort {

    OrderRoot save(OrderRoot order);

    Optional<OrderRoot> findById(OrderId id);

    Optional<OrderRoot> findAllByOrderNumber(OrderNumber orderNumber);

    List<OrderRoot> findOrderByCustomerId(CustomerId customerId);

    void delete(OrderRoot order);
}
