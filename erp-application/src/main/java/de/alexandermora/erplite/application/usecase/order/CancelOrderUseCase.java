package de.alexandermora.erplite.application.usecase.order;

import de.alexandermora.erplite.application.command.helper.CommandHelper;
import de.alexandermora.erplite.application.command.order.CancelOrderCommand;
import de.alexandermora.erplite.application.exception.CommandException;
import de.alexandermora.erplite.domain.port.repository.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Use case for canceling the status of an order.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CancelOrderUseCase {
    private final OrderRepositoryPort orderRepository;
    private final CommandHelper commandHelper;

    public void execute(CancelOrderCommand command) {
        try {
            log.info("Executing CancelOrderStatusUseCase with command: {}", command);
            var orderRoot = commandHelper.findOrderById(command.orderId());
            log.info("Current order current status: {}", orderRoot.getStatus());
            orderRoot.cancel(command.reason());
            var orderSaved = orderRepository.save(orderRoot);
            log.info("Order saved current status: {}", orderSaved.getStatus());
        } catch (Exception e) {
            log.error("Error canceling order status", e);
            throw new CommandException("Error canceling order status", e);
        }

    }
}
