package com.bookstore.ui;

import com.bookstore.model.User;
import com.bookstore.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Connection conn;
    
    public LoginFrame(Connection conn) {
        this.conn = conn;
        setTitle("图书销售管理系统 - 登录");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 用户名
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("用户名："), gbc);
        
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);
        
        // 密码
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("密码："), gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);
        
        // 登录按钮
        gbc.gridx = 1; gbc.gridy = 2;
        JButton loginButton = new JButton("登录");
        loginButton.addActionListener(e -> handleLogin());
        panel.add(loginButton, gbc);
        
        add(panel);
    }
    
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        try {
            UserService userService = new UserService(conn);
            User user = userService.login(username, password);
            
            if (user != null) {
                MainFrame mainFrame = new MainFrame(user);
                mainFrame.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "用户名或密码错误", 
                    "登录失败", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "登录失败：" + ex.getMessage(), 
                "错误", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 