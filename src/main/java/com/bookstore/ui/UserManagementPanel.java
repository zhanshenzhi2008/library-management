package com.bookstore.ui;

import com.bookstore.model.User;
import com.bookstore.service.UserService;
import com.bookstore.ui.model.UserTableModel;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private JTable userTable;
    private UserTableModel tableModel;
    private UserService userService;
    private Connection conn;
    
    public UserManagementPanel(Connection conn) {
        this.conn = conn;
        this.userService = new UserService(conn);
        
        setLayout(new BorderLayout());
        initComponents();
        loadUsers();
    }
    
    private void initComponents() {
        // 工具栏
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton addButton = new JButton("添加用户");
        JButton editButton = new JButton("编辑");
        JButton deleteButton = new JButton("删除");
        JButton resetPwdButton = new JButton("重置密码");
        JButton refreshButton = new JButton("刷新");
        
        addButton.addActionListener(e -> showUserDialog(null));
        editButton.addActionListener(e -> editSelectedUser());
        deleteButton.addActionListener(e -> deleteSelectedUser());
        resetPwdButton.addActionListener(e -> resetSelectedUserPassword());
        refreshButton.addActionListener(e -> loadUsers());
        
        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        toolBar.addSeparator();
        toolBar.add(resetPwdButton);
        toolBar.addSeparator();
        toolBar.add(refreshButton);
        
        // 用户列表
        tableModel = new UserTableModel();
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        
        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void loadUsers() {
        try {
            List<User> users = userService.getAllUsers();
            tableModel.setUsers(users);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "加载用户列表失败：" + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showUserDialog(User user) {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),
            user == null ? "添加用户" : "编辑用户",
            true);
            
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 创建表单字段
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField realNameField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JCheckBox adminCheckBox = new JCheckBox("管理员权限");
        
        // 如果是编辑模式，填充现有数据
        if (user != null) {
            usernameField.setText(user.getUsername());
            usernameField.setEnabled(false); // 用户名不允许修改
            realNameField.setText(user.getRealName());
            phoneField.setText(user.getPhone());
            emailField.setText(user.getEmail());
            adminCheckBox.setSelected(user.isAdmin());
        }
        
        // 添加表单字段到面板
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("用户名："), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        
        if (user == null) { // 只在添加新用户时显示密码字段
            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("���码："), gbc);
            gbc.gridx = 1;
            panel.add(passwordField, gbc);
        }
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("真实姓名："), gbc);
        gbc.gridx = 1;
        panel.add(realNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("电话："), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("邮箱："), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        gbc.gridx = 1; gbc.gridy = 5;
        panel.add(adminCheckBox, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");
        
        saveButton.addActionListener(e -> {
            try {
                User newUser = user == null ? new User() : user;
                newUser.setUsername(usernameField.getText());
                if (user == null) { // 只在添加新用户时设置密码
                    newUser.setPassword(new String(passwordField.getPassword()));
                }
                newUser.setRealName(realNameField.getText());
                newUser.setPhone(phoneField.getText());
                newUser.setEmail(emailField.getText());
                newUser.setAdmin(adminCheckBox.isSelected());
                
                if (user == null) {
                    userService.addUser(newUser);
                } else {
                    userService.updateUser(newUser);
                }
                
                loadUsers();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "保存失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void editSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            User user = tableModel.getUserAt(selectedRow);
            showUserDialog(user);
        } else {
            JOptionPane.showMessageDialog(this,
                "请选择要编辑的用户",
                "提示",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            User user = tableModel.getUserAt(selectedRow);
            int option = JOptionPane.showConfirmDialog(this,
                "确定要删除用户 '" + user.getUsername() + "' 吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION);
                
            if (option == JOptionPane.YES_OPTION) {
                try {
                    userService.deleteUser(user.getId());
                    loadUsers();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this,
                        "删除失败：" + e.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "请选择要删除的用户",
                "提示",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void resetSelectedUserPassword() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            User user = tableModel.getUserAt(selectedRow);
            try {
                String newPassword = userService.resetPassword(user.getId());
                JOptionPane.showMessageDialog(this,
                    "密码已重置为：" + newPassword,
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "重置密码失败：" + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "请选择要重置密码的用户",
                "提示",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
} 