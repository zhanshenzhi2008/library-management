package com.bookstore.dao;

import com.bookstore.model.Order;
import com.bookstore.model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {
    private Connection conn;
    private OrderItemDao orderItemDao;
    
    public OrderDao(Connection conn) {
        this.conn = conn;
        this.orderItemDao = new OrderItemDao(conn);
    }
    
    public void insert(Order order) throws SQLException {
        String sql = "INSERT INTO orders (user_id, username, total_amount, status, create_time) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, order.getUserId());
            stmt.setString(2, order.getUsername());
            stmt.setBigDecimal(3, order.getTotalAmount());
            stmt.setString(4, order.getStatus());
            stmt.setTimestamp(5, Timestamp.valueOf(order.getOrderDate()));
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    order.setId(rs.getLong(1));
                    // Save order items
                    if (order.getItems() != null) {
                        for (OrderItem item : order.getItems()) {
                            item.setOrderId(order.getId());
                            orderItemDao.insert(item);
                        }
                    }
                }
            }
        }
    }
    
    public List<Order> findByUserId(Long userId) throws SQLException {
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY create_time DESC";
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    // Load order items
                    order.setItems(orderItemDao.findByOrderId(order.getId()));
                    orders.add(order);
                }
            }
        }
        return orders;
    }
    
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setUserId(rs.getLong("user_id"));
        order.setUsername(rs.getString("username"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setStatus(rs.getString("status"));
        order.setOrderDate(rs.getTimestamp("create_time").toLocalDateTime());
        return order;
    }
} 