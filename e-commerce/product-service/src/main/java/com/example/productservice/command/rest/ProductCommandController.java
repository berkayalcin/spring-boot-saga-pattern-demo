package com.example.productservice.command.rest;

import com.example.productservice.command.model.CreateProductCommand;
import com.example.productservice.command.model.CreateProductRequest;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductCommandController {
    private final CommandGateway commandGateway;

    @PostMapping
    public String create(@RequestBody @Valid final CreateProductRequest createProductRequest) {
        final var createProductCommand = buildCreateProductCommand(createProductRequest);

        final var result = (String) commandGateway.sendAndWait(createProductCommand);
        return "Http POST Handled " + result;
    }

    private CreateProductCommand buildCreateProductCommand(final CreateProductRequest createProductRequest) {
        return CreateProductCommand.builder()
                .price(createProductRequest.getPrice())
                .quantity(createProductRequest.getQuantity())
                .name(createProductRequest.getName())
                .id(UUID.randomUUID().toString())
                .build();
    }
}
