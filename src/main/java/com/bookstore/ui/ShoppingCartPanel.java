package com.bookstore.ui;

import com.bookstore.model.CartItem;
import com.bookstore.model.Order;
import com.bookstore.model.ShoppingCart;
import com.bookstore.model.User;
import com.bookstore.service.OrderService;
import com.bookstore.ui.model.CartTableModel;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class ShoppingCartPanel extends JPanel {
    private JTable cartTable;
    private CartTableModel tableModel;
    private ShoppingCart shoppingCart;
    private OrderService orderService;
    private User currentUser;
    
    public ShoppingCartPanel(Connection conn, User user, ShoppingCart cart) {
        this.currentUser = user;
        this.shoppingCart = cart;
        this.orderService = new OrderService(conn);
        
        setLayout(new BorderLayout());
        initComponents();
    }
    
    private void initComponents() {
        // 购物车列表
        tableModel = new CartTableModel(shoppingCart);
        cartTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(cartTable);
        
        // 操作按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton removeButton = new JButton("移除");
        JButton checkoutButton = new JButton("结算");
        
        removeButton.addActionListener(e -> removeSelectedItem());
        checkoutButton.addActionListener(e -> checkout());
        
        buttonPanel.add(removeButton);
        buttonPanel.add(checkoutButton);
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void removeSelectedItem() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow >= 0) {
            CartItem item = tableModel.getItemAt(selectedRow);
            shoppingCart.removeItem(item);
            tableModel.fireTableDataChanged();
        }
    }
    
    private void checkout() {
        if (shoppingCart.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "购物车是空的", 
                "提示", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            Order order = new Order();
            order.setUserId(currentUser.getId());
            order.setOrderItems(shoppingCart.toOrderItems());
            
            orderService.createOrder(order);
            shoppingCart.clear();
            tableModel.fireTableDataChanged();
            
            JOptionPane.showMessageDialog(this, 
                "订单创建成功", 
                "提示", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "创建订单失败：" + e.getMessage(), 
                "错误", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 