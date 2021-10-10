package com.example.productservice.core.repository;

import com.example.productservice.core.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
    Product findProductById(final String productId);
}
