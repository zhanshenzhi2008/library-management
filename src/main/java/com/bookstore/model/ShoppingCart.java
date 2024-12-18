package com.bookstore.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<CartItem> items = new ArrayList<>();
    
    public void addItem(Book book, int quantity) {
        // 检查是否已存在
        for (CartItem item : items) {
            if (item.getBook().getId().equals(book.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        // 不存在则添加新项
        items.add(new CartItem(book, quantity));
    }
    
    public void removeItem(CartItem item) {
        items.remove(item);
    }
    
    public void clear() {
        items.clear();
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    public List<CartItem> getItems() {
        return items;
    }
    
    public BigDecimal getTotalAmount() {
        return items.stream()
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public List<OrderItem> toOrderItems() {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setBookId(cartItem.getBook().getId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getBook().getPrice());
            orderItems.add(orderItem);
        }
        return orderItems;
    }
} 