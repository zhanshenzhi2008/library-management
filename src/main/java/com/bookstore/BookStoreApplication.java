package com.bookstore;

import com.bookstore.config.DatabaseConfig;
import com.bookstore.ui.LoginFrame;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class BookStoreApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 获取数据库连接
                Connection conn = DatabaseConfig.getConnection();

                // 创建登录窗口
                LoginFrame loginFrame = new LoginFrame(conn);
                loginFrame.setVisible(true);
            } catch (SQLException e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
    
    // 在应用程序关闭时关闭连接池
    public static void shutdown() {
        DatabaseConfig.closeDataSource();
    }
} 