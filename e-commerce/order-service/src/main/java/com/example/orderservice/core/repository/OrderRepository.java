package com.example.orderservice.core.repository;

import com.example.orderservice.core.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
    Order findOrderById(final String orderId);
}
