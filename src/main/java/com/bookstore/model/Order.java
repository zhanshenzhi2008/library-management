package com.bookstore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id;
    private Long userId;
    private String username;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String status;
    private List<OrderItem> items;

    public Order(Long userId, String username) {
        this.userId = userId;
        this.username = username;
        this.orderDate = LocalDateTime.now();
        this.status = "PENDING";
        this.totalAmount = BigDecimal.ZERO;
    }
} 