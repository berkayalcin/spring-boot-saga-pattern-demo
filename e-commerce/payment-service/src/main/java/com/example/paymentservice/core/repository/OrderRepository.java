package com.example.paymentservice.core.repository;

import com.example.paymentservice.core.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
    Order findOrderById(final String orderId);
}
