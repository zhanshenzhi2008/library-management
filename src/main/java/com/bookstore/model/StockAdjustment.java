package com.bookstore.model;

public class StockAdjustment {
    private String isbn;
    private int adjustment;
    
    public StockAdjustment(String isbn, int adjustment) {
        this.isbn = isbn;
        this.adjustment = adjustment;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public int getAdjustment() {
        return adjustment;
    }
} 