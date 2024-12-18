package com.bookstore.ui;

import com.bookstore.model.Book;
import com.bookstore.model.ShoppingCart;
import com.bookstore.model.User;
import com.bookstore.model.UserRole;
import com.bookstore.service.BookService;
import com.bookstore.ui.model.BookTableModel;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BookListPanel extends JPanel {
    private JTable bookTable;
    private BookTableModel tableModel;
    private BookService bookService;
    private User currentUser;
    private ShoppingCart shoppingCart;
    
    public BookListPanel(Connection conn, User user, ShoppingCart cart) {
        this.currentUser = user;
        this.bookService = new BookService(conn);
        this.shoppingCart = cart;
        
        setLayout(new BorderLayout());
        initComponents();
        loadBooks();
    }
    
    private void initComponents() {
        // 搜索面板
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"全部", "文学", "计算机", "经济"});
        JButton searchButton = new JButton("搜索");
        
        searchPanel.add(new JLabel("分类："));
        searchPanel.add(categoryCombo);
        searchPanel.add(new JLabel("关键字："));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // 图书列表
        tableModel = new BookTableModel();
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        
        // 操作按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addToCartButton = new JButton("加入购物车");
        addToCartButton.addActionListener(e -> addToCart());
        buttonPanel.add(addToCartButton);
        
        if (currentUser.getRole().equals(UserRole.ADMIN)) {
            JButton addBookButton = new JButton("添加图书");
            JButton editBookButton = new JButton("编辑图书");
            buttonPanel.add(addBookButton);
            buttonPanel.add(editBookButton);
        }
        
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadBooks() {
        try {
            List<Book> books = bookService.getAllBooks();
            tableModel.setBooks(books);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "加载图书列表失败：" + e.getMessage(), 
                "错误", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addToCart() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            Book book = tableModel.getBookAt(selectedRow);
            String quantityStr = JOptionPane.showInputDialog(this, 
                "请输入购买数量：", 
                "加入购物车", 
                JOptionPane.QUESTION_MESSAGE);
            
            try {
                int quantity = Integer.parseInt(quantityStr);
                if (quantity > 0 && quantity <= book.getStock()) {
                    shoppingCart.addItem(book, quantity);
                    JOptionPane.showMessageDialog(this,
                        "成功加入购物车",
                        "提示",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "无效的购买数量", 
                        "错误", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "请输入有效的数字", 
                    "错误", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "请选择要购买的图书",
                "提示",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
} 