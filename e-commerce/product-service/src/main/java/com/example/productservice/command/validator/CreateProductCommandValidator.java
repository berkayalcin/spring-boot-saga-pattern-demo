package com.example.productservice.command.validator;

import com.example.productservice.command.model.CreateProductCommand;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CreateProductCommandValidator {
    public void validate(final CreateProductCommand createProductCommand) {
        if (createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price cannot be less or equal than zero");
        }
        if (createProductCommand.getName() == null || createProductCommand.getName().isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
    }
}
