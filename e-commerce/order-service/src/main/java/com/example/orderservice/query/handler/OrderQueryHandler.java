package com.example.orderservice.query.handler;

import com.example.orderservice.core.entity.Order;
import com.example.orderservice.core.repository.OrderRepository;
import com.example.orderservice.query.model.FindOrderQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderQueryHandler {
    private final OrderRepository orderRepository;

    @QueryHandler
    public Order on(final FindOrderQuery findOrderQuery) {
        return orderRepository.findOrderById(findOrderQuery.getOrderId());
    }
}
