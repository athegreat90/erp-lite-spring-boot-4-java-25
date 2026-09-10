package de.alexandermora.erplite.application.usecase.order;


import de.alexandermora.erplite.application.command.order.CreateOrderCommand;
import de.alexandermora.erplite.application.exception.CommandException;
import de.alexandermora.erplite.domain.entity.order.Customer;
import de.alexandermora.erplite.domain.entity.order.OrderItem;
import de.alexandermora.erplite.domain.entity.order.OrderNumber;
import de.alexandermora.erplite.domain.entity.order.OrderRoot;
import de.alexandermora.erplite.domain.entity.product.ProductId;
import de.alexandermora.erplite.domain.port.repository.OrderRepositoryPort;
import de.alexandermora.erplite.domain.port.repository.ProductRepositoryPort;
import de.alexandermora.erplite.domain.port.service.CustomerProviderServicePort;
import de.alexandermora.erplite.domain.port.service.OrderConfirmEmailServicePort;
import de.alexandermora.erplite.domain.shared.CustomerId;
import de.alexandermora.erplite.domain.shared.Email;
import de.alexandermora.erplite.domain.shared.Quantity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Use case for creating an order.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CreateOrderUseCase {
    private final OrderRepositoryPort orderRepository;
    private final ProductRepositoryPort productRepository;
    private final CustomerProviderServicePort customerProviderService;
    private final OrderConfirmEmailServicePort emailService;

    public String execute(CreateOrderCommand command) {
        try {
            log.info("Executing CreateOrderUseCase for customer ID: {}", command.customerId());
            var customer = validateAndGet(command.customerId());
            var orderItems = createOrderItems(command.items());
            var orderNumber = generateOrderNumber();
            var orderRoot = OrderRoot.create(orderNumber, customer, orderItems, command.createdBy());
            var savedOrder = orderRepository.save(orderRoot);

            log.info("Order created with ID: {} and Order Number: {}", savedOrder.getId(), orderNumber.value());
            sendEmail(savedOrder, customer);
            return savedOrder.getId().toString();
        } catch (IllegalArgumentException e) {
            log.error("Invalid data", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred", e);
            throw new CommandException(e.getMessage(), e);
        }
    }

    private Customer validateAndGet(Long customerId) {
        log.info("Validating customer with ID: {}", customerId);
        var customerInfo = customerProviderService.findById(customerId)
                .orElseThrow(() -> new CommandException("Customer with ID " + customerId + " not found"));
        return Customer.of(CustomerId.of(customerId), customerInfo.name());
    }

    private List<OrderItem> createOrderItems(List<CreateOrderCommand.OrderItemRequest> commandItems) {
        log.info("Creating order items from command items: {}", commandItems.size());
        return commandItems.stream()
                .map(this::toOrderItem)
                .toList();
    }

    private OrderItem toOrderItem(CreateOrderCommand.OrderItemRequest commandItem) {
        var productRoot = productRepository.findById(ProductId.of(UUID.fromString(commandItem.productId()))).orElseThrow(
                () -> new CommandException("Product with ID " + commandItem.productId() + " not found")
        );
        var quantity = Quantity.of(commandItem.quantity());
        return OrderItem.from(productRoot, quantity);
    }

    private OrderNumber generateOrderNumber() {
        return OrderNumber.generate((int) (System.currentTimeMillis() % 1000));
    }

    private void publishDomainEvent(OrderRoot order) {
        var events = order.getDomainEvents();
        log.info("Publishing {} domain events for order {}", events.size(), order.getId());
        events.forEach(event -> {
            log.debug("Try to Publish domain event: {}", event);
            // Here you would publish the event to your event bus or message broker
            // TODO: Implement event publishing logic
        });

        order.clearDomainEvents();
        log.info("Published {} domain events for order {}", events.size(), order.getId());
    }

    private void sendEmail(OrderRoot order, Customer customer) {
        log.info("Sending order confirmation email for order {} to customer {}", order.getId(), customer.customerName());
        try {
            emailService.sendMail(Email.of("alexander.mora@proton.me"), order.getId(), order.getOrderNumber().value(), order.getTotalAmount(), customer.customerName(), order.getItems().size());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
