package com.bookstore.dao;

import com.bookstore.model.OrderItem;
import com.bookstore.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDao {
    private Connection conn;
    private BookDao bookDao;
    
    public OrderItemDao(Connection conn) {
        this.conn = conn;
        this.bookDao = new BookDao(conn);
    }
    
    public void insert(OrderItem item) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, book_id, isbn, quantity, price, book_title) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, item.getOrderId());
            stmt.setLong(2, item.getBookId());
            stmt.setString(3, item.getIsbn());
            stmt.setInt(4, item.getQuantity());
            stmt.setBigDecimal(5, item.getPrice());
            stmt.setString(6, item.getBookTitle());
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setId(rs.getLong(1));
                }
            }
        }
    }
    
    public List<OrderItem> findByOrderId(Long orderId) throws SQLException {
        String sql = "SELECT oi.*, b.title as book_title, b.isbn " +
                    "FROM order_items oi " +
                    "LEFT JOIN books b ON oi.book_id = b.id " +
                    "WHERE oi.order_id = ?";
        List<OrderItem> items = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToOrderItem(rs));
                }
            }
        }
        return items;
    }
    
    private OrderItem mapResultSetToOrderItem(ResultSet rs) throws SQLException {
        OrderItem item = new OrderItem();
        item.setId(rs.getLong("id"));
        item.setOrderId(rs.getLong("order_id"));
        item.setBookId(rs.getLong("book_id"));
        item.setIsbn(rs.getString("isbn"));
        item.setQuantity(rs.getInt("quantity"));
        item.setPrice(rs.getBigDecimal("price"));
        item.setBookTitle(rs.getString("book_title"));
        return item;
    }
} 