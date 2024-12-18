package com.bookstore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long bookId;
    private String isbn;
    private int quantity;
    private BigDecimal price;
    private String bookTitle;
    
    public OrderItem(Long bookId, String isbn, String bookTitle, int quantity, BigDecimal price) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.bookTitle = bookTitle;
        this.quantity = quantity;
        this.price = price;
    }
    
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
} 