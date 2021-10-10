package com.example.productservice.event.handler;

import com.example.productservice.core.entity.Product;
import com.example.productservice.core.repository.ProductRepository;
import com.example.productservice.event.model.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup(value = "product-group")
@Slf4j
public class ProductEventHandler {
    private final ProductRepository productRepository;

    @SneakyThrows
    @EventHandler
    public void on(final ProductCreatedEvent productCreatedEvent) {
        final var product = Product.builder()
                .id(productCreatedEvent.getId())
                .price(productCreatedEvent.getPrice())
                .quantity(productCreatedEvent.getQuantity())
                .name(productCreatedEvent.getName())
                .build();

        productRepository.save(product);
    }

}
