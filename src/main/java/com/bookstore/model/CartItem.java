package com.bookstore.model;

import java.math.BigDecimal;

public class CartItem {
    private Book book;
    private int quantity;
    
    public CartItem(Book book, int quantity) {
        this.book = book;
        this.quantity = quantity;
    }
    
    public Book getBook() {
        return book;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getSubtotal() {
        return book.getPrice().multiply(new BigDecimal(quantity));
    }
} 