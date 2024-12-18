package com.bookstore.service;

import com.bookstore.dao.BookDao;
import com.bookstore.dao.OrderDao;
import com.bookstore.model.Order;
import com.bookstore.util.TransactionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderService {
    private Connection conn;
    private OrderDao orderDao;
    private BookDao bookDao;
    private TransactionManager transactionManager;
    
    public OrderService(Connection conn) {
        this.conn = conn;
        this.orderDao = new OrderDao(conn);
        this.bookDao = new BookDao(conn);
        this.transactionManager = new TransactionManager(conn);
    }
    
    public void createOrder(Order order) throws SQLException {
        try {
            transactionManager.beginTransaction();
            
            // 验证订单信息
            if (order.getUserId() == null) {
                throw new IllegalArgumentException("用户ID不能为空");
            }
            if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
                throw new IllegalArgumentException("订单项不能为空");
            }
            
            // 计算订单总金额并更新库存
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (OrderItem item : order.getOrderItems()) {
                Book book = bookDao.findById(item.getBookId());
                if (book == null) {
                    throw new IllegalArgumentException("图书不存在：" + item.getBookId());
                }
                if (book.getStock() < item.getQuantity()) {
                    throw new IllegalArgumentException("图书库存不足：" + book.getTitle());
                }
                
                item.setPrice(book.getPrice());
                totalAmount = totalAmount.add(book.getPrice().multiply(new BigDecimal(item.getQuantity())));
                
                book.setStock(book.getStock() - item.getQuantity());
                bookDao.update(book);
            }
            
            order.setTotalAmount(totalAmount);
            order.setStatus("PENDING");
            orderDao.insert(order);
            
            transactionManager.commit();
        } catch (Exception e) {
            transactionManager.rollback();
            throw e;
        }
    }
    
    public List<Order> getUserOrders(Integer userId) throws SQLException {
        return orderDao.findByUserId(userId);
    }
} 