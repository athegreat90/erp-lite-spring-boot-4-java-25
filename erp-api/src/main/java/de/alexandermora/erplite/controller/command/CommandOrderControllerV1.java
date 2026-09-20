package de.alexandermora.erplite.controller.command;

import de.alexandermora.erplite.application.command.order.CancelOrderCommand;
import de.alexandermora.erplite.application.command.order.CreateOrderCommand;
import de.alexandermora.erplite.application.command.order.UpdateOrderStatusCommand;
import de.alexandermora.erplite.application.usecase.order.CancelOrderUseCase;
import de.alexandermora.erplite.application.usecase.order.CreateOrderUseCase;
import de.alexandermora.erplite.application.usecase.order.UpdateOrderStatusUseCase;
import de.alexandermora.erplite.paths.ApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = ApiPaths.COMMANDS_ORDERS, version = "1")
@RequiredArgsConstructor
@Tag(name = "Command Orders", description = "Command endpoints for order management")
public class CommandOrderControllerV1 {

    private final CreateOrderUseCase createOrderUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;

    @Operation(summary = "Create order", description = "Creates a new purchase order in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid order data"),
            @ApiResponse(responseCode = "409", description = "Business rule violation"),
            @ApiResponse(responseCode = "422", description = "Error processing the command"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void postOrder(
            @Parameter(description = "Order data to create", required = true)
            @Valid @RequestBody CreateOrderCommand createOrderCommand,
            HttpServletResponse response) {

        log.info("POST order");

        String orderId = this.createOrderUseCase.execute(createOrderCommand);

        response.setHeader("Location", ApiPaths.COMMANDS_ORDERS + "/" + orderId);
    }

    @Operation(summary = "Cancel order", description = "Cancels an existing order indicating the cancellation reason")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "409", description = "Business rule violation"),
            @ApiResponse(responseCode = "422", description = "Error processing the command"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping(path = "/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patchOrderCancel(
            @Parameter(description = "Unique order identifier", required = true, example = "order-001")
            @PathVariable String id,
            @Parameter(description = "Cancellation reason", required = true, example = "Customer requested cancellation")
            @RequestParam String reason) {

        log.info("PATCH cancel order");

        var command = new CancelOrderCommand(id, reason);

        this.cancelOrderUseCase.execute(command);
    }

    @Operation(summary = "Update order status", description = "Changes the current status of an order (e.g. CONFIRMED, SHIPPED, DELIVERED)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "409", description = "Business rule violation"),
            @ApiResponse(responseCode = "422", description = "Error processing the command"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping(path = "/{id}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patchOrderStatus(
            @Parameter(description = "Unique order identifier", required = true, example = "order-001")
            @PathVariable String id,
            @Parameter(description = "New order status", required = true, example = "SHIPPED")
            @RequestParam String status) {

        log.info("PATCH order status");

        var command = new UpdateOrderStatusCommand(id, status);

        this.updateOrderStatusUseCase.execute(command);
    }
}
