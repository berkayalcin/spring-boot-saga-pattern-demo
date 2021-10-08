package com.example.productservice.core.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "products")
public class Product {
    @Id
    @Column(unique = true)
    private String id;

    @Column(unique = true)
    private String name;
    private BigDecimal price;
    private int quantity;
}
