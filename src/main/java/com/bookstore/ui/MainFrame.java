package com.bookstore.ui;

import com.bookstore.config.DatabaseConfig;
import com.bookstore.model.ShoppingCart;
import com.bookstore.model.User;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class MainFrame extends JFrame {
    private User currentUser;
    private Connection conn;
    private ShoppingCart shoppingCart;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    
    public MainFrame(User user) {
        this.currentUser = user;
        this.shoppingCart = new ShoppingCart();
        
        try {
            this.conn = DatabaseConfig.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "数据库连接失败：" + e.getMessage(), 
                "错误", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        setTitle("图书销售管理系统 - " + user.getUsername());
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        initComponents();
    }
    
    private void initComponents() {
        // 创建菜单栏
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);
        
        // 创建主容器面板
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // 添加各个功能面板
        contentPanel.add(new BookListPanel(conn, currentUser, shoppingCart), "bookList");
        contentPanel.add(new ShoppingCartPanel(conn, currentUser, shoppingCart), "cart");
        contentPanel.add(new OrderListPanel(conn, currentUser), "orderList");
        
        if (currentUser.isAdmin()) {
            contentPanel.add(new BookManagementPanel(conn), "bookManagement");
            contentPanel.add(new UserManagementPanel(conn), "userManagement");
            contentPanel.add(new SalesStatisticsPanel(conn), "statistics");
        }
        
        add(contentPanel);
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // 图书菜单
        JMenu bookMenu = new JMenu("图书");
        JMenuItem browseBooks = new JMenuItem("浏览图书");
        browseBooks.addActionListener(e -> cardLayout.show(contentPanel, "bookList"));
        bookMenu.add(browseBooks);
        
        if (currentUser.isAdmin()) {
            bookMenu.addSeparator();
            JMenuItem manageBooks = new JMenuItem("图书管理");
            manageBooks.addActionListener(e -> cardLayout.show(contentPanel, "bookManagement"));
            bookMenu.add(manageBooks);
        }
        
        // 订单菜单
        JMenu orderMenu = new JMenu("订单");
        JMenuItem cart = new JMenuItem("购物车");
        cart.addActionListener(e -> cardLayout.show(contentPanel, "cart"));
        JMenuItem orderHistory = new JMenuItem("订单历史");
        orderHistory.addActionListener(e -> cardLayout.show(contentPanel, "orderList"));
        orderMenu.add(cart);
        orderMenu.add(orderHistory);
        
        if (currentUser.isAdmin()) {
            orderMenu.addSeparator();
            JMenuItem statistics = new JMenuItem("销售统计");
            statistics.addActionListener(e -> cardLayout.show(contentPanel, "statistics"));
            orderMenu.add(statistics);
        }
        
        // 用户菜单
        JMenu userMenu = new JMenu("用户");
        JMenuItem profile = new JMenuItem("个人信息");
        profile.addActionListener(e -> showProfileDialog());
        userMenu.add(profile);
        
        if (currentUser.isAdmin()) {
            userMenu.addSeparator();
            JMenuItem manageUsers = new JMenuItem("用户管理");
            manageUsers.addActionListener(e -> cardLayout.show(contentPanel, "userManagement"));
            userMenu.add(manageUsers);
        }
        
        menuBar.add(bookMenu);
        menuBar.add(orderMenu);
        menuBar.add(userMenu);
        
        return menuBar;
    }
    
    private void showProfileDialog() {
        // TODO: 实现个人信息编辑对话框
    }
} 