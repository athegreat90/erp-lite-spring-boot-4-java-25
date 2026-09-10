package de.alexandermora.erplite.application.usecase.order;

import de.alexandermora.erplite.application.command.helper.CommandHelper;
import de.alexandermora.erplite.application.command.order.UpdateOrderStatusCommand;
import de.alexandermora.erplite.application.exception.CommandException;
import de.alexandermora.erplite.domain.entity.order.OrderRoot;
import de.alexandermora.erplite.domain.port.repository.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for updating the status of an order.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UpdateOrderStatusUseCase {
    private final OrderRepositoryPort orderRepository;
    private final CommandHelper commandHelper;

    public String execute(UpdateOrderStatusCommand command) throws CommandException {
        try {
            var orderRoot = commandHelper.findOrderById(command.orderId());
            log.info("Current order current status: {}", orderRoot.getStatus());
            updateStatus(orderRoot, command.newStatus());
            var orderSaved = orderRepository.save(orderRoot);
            log.info("Order saved current status: {}", orderSaved.getStatus());
            return orderSaved.getStatus().toString();
        } catch (IllegalStateException ise) {
            log.error("Error updating order status", ise);
            throw new CommandException("Error updating order status", ise);
        } catch (Exception e) {
            log.error("Error updating order status", e);
            throw new CommandException("Unexpected error updating order status", e);
        }
    }

    private void updateStatus(OrderRoot order, String status) {
        switch (status.toUpperCase()) {
            case "CONFIRMED" -> order.confirm();
            case "SHIPPED" -> order.ship();
            case "DELIVERED" -> order.deliver();
            default -> throw new CommandException("Invalid status " + status);
        }
    }
}
