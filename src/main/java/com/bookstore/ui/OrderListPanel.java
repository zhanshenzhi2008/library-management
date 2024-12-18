package com.bookstore.ui;

import com.bookstore.model.Order;
import com.bookstore.model.User;
import com.bookstore.service.OrderService;
import com.bookstore.ui.model.OrderTableModel;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderListPanel extends JPanel {
    private JTable orderTable;
    private OrderTableModel tableModel;
    private OrderService orderService;
    private User currentUser;
    private JPanel detailPanel;
    
    public OrderListPanel(Connection conn, User user) {
        this.currentUser = user;
        this.orderService = new OrderService(conn);
        
        setLayout(new BorderLayout());
        initComponents();
        loadOrders();
    }
    
    private void initComponents() {
        // 创建分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400);
        
        // 订单列表面板
        JPanel listPanel = new JPanel(new BorderLayout());
        tableModel = new OrderTableModel();
        orderTable = new JTable(tableModel);
        orderTable.getSelectionModel().addListSelectionListener(e -> showOrderDetail());
        JScrollPane scrollPane = new JScrollPane(orderTable);
        listPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 订单详情面板
        detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createTitledBorder("订单详情"));
        
        splitPane.setTopComponent(listPanel);
        splitPane.setBottomComponent(detailPanel);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private void loadOrders() {
        try {
            List<Order> orders = orderService.getUserOrders(currentUser.getId());
            tableModel.setOrders(orders);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "加载订单列表失败：" + e.getMessage(), 
                "错误", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showOrderDetail() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow >= 0) {
            Order order = tableModel.getOrderAt(selectedRow);
            detailPanel.removeAll();
            
            // 创建订单详情表格
            OrderItemTableModel detailModel = new OrderItemTableModel(order.getOrderItems());
            JTable detailTable = new JTable(detailModel);
            JScrollPane scrollPane = new JScrollPane(detailTable);
            
            // 添加订单信息
            JPanel infoPanel = new JPanel(new GridLayout(3, 2, 5, 5));
            infoPanel.add(new JLabel("订单编号："));
            infoPanel.add(new JLabel(order.getId().toString()));
            infoPanel.add(new JLabel("订单状态："));
            infoPanel.add(new JLabel(order.getStatus()));
            infoPanel.add(new JLabel("订单总额："));
            infoPanel.add(new JLabel(order.getTotalAmount().toString()));
            
            detailPanel.add(infoPanel, BorderLayout.NORTH);
            detailPanel.add(scrollPane, BorderLayout.CENTER);
            
            detailPanel.revalidate();
            detailPanel.repaint();
        }
    }
} 