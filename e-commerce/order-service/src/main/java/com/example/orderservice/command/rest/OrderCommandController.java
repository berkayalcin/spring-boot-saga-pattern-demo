package com.example.orderservice.command.rest;

import com.example.orderservice.command.model.CreateOrderCommand;
import com.example.orderservice.command.model.CreateOrderRequest;
import com.example.orderservice.core.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderCommandController {
    private final CommandGateway commandGateway;

    @PostMapping
    public String create(@RequestBody final CreateOrderRequest createOrderRequest) {
        final var createOrderCommand = buildCreateOrderCommand(createOrderRequest);
        return commandGateway.sendAndWait(createOrderCommand);
    }

    private CreateOrderCommand buildCreateOrderCommand(final CreateOrderRequest createOrderRequest) {
        return CreateOrderCommand.builder()
                .orderStatus(OrderStatus.CREATED)
                .addressId(createOrderRequest.getAddressId())
                .id(UUID.randomUUID().toString())
                .productId(createOrderRequest.getProductId())
                .userId("27b95829-4f3f-4ddf-8983-151ba010e35b")
                .quantity(createOrderRequest.getQuantity())
                .build();
    }
}
