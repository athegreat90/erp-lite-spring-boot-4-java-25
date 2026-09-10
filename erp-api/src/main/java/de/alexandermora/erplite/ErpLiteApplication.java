package de.alexandermora.erplite;

import de.alexandermora.erplite.application.command.order.CancelOrderCommand;
import de.alexandermora.erplite.application.command.order.CreateOrderCommand;
import de.alexandermora.erplite.application.command.order.UpdateOrderStatusCommand;
import de.alexandermora.erplite.application.usecase.order.CancelOrderUseCase;
import de.alexandermora.erplite.application.usecase.order.CreateOrderUseCase;
import de.alexandermora.erplite.application.usecase.order.UpdateOrderStatusUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class ErpLiteApplication implements CommandLineRunner {


    private final CreateOrderUseCase createOrderUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;


    public static void main(String[] args) {
        SpringApplication.run(ErpLiteApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info(" Testing ERP Use Cases");
        log.info("|----------------------------|");

        try {

            // Test 1: Create Order
//            String createdOrderId = testCreateOrder();
//            log.info("Created with id: {}", createdOrderId);

            // Test 2: Update Order
            String createdOrderId2 = "a87b8299-efb2-4681-8f12-2db75c7489f8";
//            testUpdateOrderStatus(createdOrderId2);

            // Test 3: Cancel Order
            testCancelOrder(createdOrderId2);


        } catch (Exception e) {
            log.error("Test failed", e);
        }

    }

    private String testCreateOrder() {
        log.info("TEST 1: CREATE ORDER");


        var command = new CreateOrderCommand(
                1L,  // customerId - Leanne Graham from JSONPlaceholder
                List.of(  // items - Lista de productos
                        // Laptop Dell XPS 15 - 1 unit - $1,499.99
                        new CreateOrderCommand.OrderItemRequest("11111111-1111-1111-1111-111111111111", 1),
                        // Mechanical Keyboard RGB - 2 units - $149.99 x 2 = $299.98
                        new CreateOrderCommand.OrderItemRequest("66666666-6666-6666-6666-666666666666", 2),
                        // Logitech MX Master 3S - 1 unit - $99.99
                        new CreateOrderCommand.OrderItemRequest("77777777-7777-7777-7777-777777777777", 1)
                ),
                "admin"  // createdBy - Usuario que crea la orden
        );

        return createOrderUseCase.execute(command);
    }

    private void testUpdateOrderStatus(String orderId) {

        log.info("TEST 2: UPDATE ORDER STATUS");

        var command = new UpdateOrderStatusCommand(orderId, "CONFIRMED");

        log.info("Updating order {} to status: CONFIRMED", orderId);

        updateOrderStatusUseCase.execute(command);

    }

    /**
     * Test 3: Cancel an existing order
     * Using an existing order from seed data: ORD-2025-004 (PENDING)
     */
    private void testCancelOrder(String orderId) {
        log.info(" TEST 3: CANCEL ORDER");

        var command = new CancelOrderCommand(orderId, "Customer requested cancellation - testing use case");

        cancelOrderUseCase.execute(command);

    }

}
