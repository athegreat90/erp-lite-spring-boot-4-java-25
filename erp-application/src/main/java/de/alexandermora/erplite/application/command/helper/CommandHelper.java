package de.alexandermora.erplite.application.command.helper;

import de.alexandermora.erplite.application.exception.CommandException;
import de.alexandermora.erplite.domain.entity.order.OrderId;
import de.alexandermora.erplite.domain.entity.order.OrderRoot;
import de.alexandermora.erplite.domain.port.repository.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandHelper {
    private final OrderRepositoryPort orderRepository;

    public OrderRoot findOrderById(String orderId) {
        log.info("Finding order by id {}", orderId);
        return this.orderRepository.findById(OrderId.of(UUID.fromString(orderId)))
                .orElseThrow(() -> new CommandException("Order not found with id " + orderId));
    }

}
