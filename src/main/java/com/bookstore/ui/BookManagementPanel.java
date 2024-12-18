package com.bookstore.ui;

import com.bookstore.model.Book;
import com.bookstore.service.BookService;
import com.bookstore.ui.model.BookTableModel;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

public class BookManagementPanel extends JPanel {
    private JTable bookTable;
    private BookTableModel tableModel;
    private BookService bookService;
    private Connection conn;
    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> sortCombo;
    
    public BookManagementPanel(Connection conn) {
        this.conn = conn;
        this.bookService = new BookService(conn);
        
        setLayout(new BorderLayout());
        initComponents();
        loadBooks();
    }
    
    private void initComponents() {
        // 搜索和过滤面板
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        searchField = new JTextField(20);
        categoryCombo = new JComboBox<>(new String[]{"全部", "文学", "计算机", "经济"});
        sortCombo = new JComboBox<>(new String[]{"默认排序", "价格升序", "价格降序", "库存升序", "库存降序"});
        JButton searchButton = new JButton("搜索");
        
        searchButton.addActionListener(e -> searchBooks());
        searchField.addActionListener(e -> searchBooks()); // 按回车搜索
        categoryCombo.addActionListener(e -> searchBooks());
        sortCombo.addActionListener(e -> sortBooks());
        
        searchPanel.add(new JLabel("关键字："));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("分类："));
        searchPanel.add(categoryCombo);
        searchPanel.add(new JLabel("排序："));
        searchPanel.add(sortCombo);
        searchPanel.add(searchButton);
        
        // 工具栏
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton addButton = new JButton("添加图书");
        JButton editButton = new JButton("编辑");
        JButton deleteButton = new JButton("删除");
        JButton refreshButton = new JButton("刷新");
        
        addButton.addActionListener(e -> showBookDialog(null));
        editButton.addActionListener(e -> editSelectedBook());
        deleteButton.addActionListener(e -> deleteSelectedBook());
        refreshButton.addActionListener(e -> loadBooks());
        
        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        toolBar.addSeparator();
        toolBar.add(refreshButton);
        
        // 创建一个面板包含搜索面板和工具栏
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(toolBar, BorderLayout.SOUTH);
        
        // 图书列表
        tableModel = new BookTableModel();
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        
        // 设置表格列宽
        bookTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        bookTable.getColumnModel().getColumn(1).setPreferredWidth(200); // 书名
        bookTable.getColumnModel().getColumn(2).setPreferredWidth(100); // 作者
        bookTable.getColumnModel().getColumn(3).setPreferredWidth(100); // ISBN
        bookTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // 分类
        bookTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // 价格
        bookTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // 库存
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // 添加库存预警提示
        checkLowStock();
    }
    
    private void loadBooks() {
        try {
            tableModel.setBooks(bookService.getAllBooks());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "加载图书列表失败：" + e.getMessage(), 
                "错误", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showBookDialog(Book book) {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), 
            book == null ? "添加图书" : "编辑图书", 
            true);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 创建表单字段
        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JTextField isbnField = new JTextField(20);
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"文学", "计算机", "经济"});
        JTextField priceField = new JTextField(20);
        JTextField stockField = new JTextField(20);
        JTextArea descArea = new JTextArea(3, 20);
        
        // 如果是编辑模式，填充现有数据
        if (book != null) {
            titleField.setText(book.getTitle());
            authorField.setText(book.getAuthor());
            isbnField.setText(book.getIsbn());
            categoryCombo.setSelectedItem(book.getCategory());
            priceField.setText(book.getPrice().toString());
            stockField.setText(book.getStock().toString());
            descArea.setText(book.getDescription());
        }
        
        // 添加表单字段到面板
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("书名："), gbc);
        gbc.gridx = 1;
        panel.add(titleField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("作者："), gbc);
        gbc.gridx = 1;
        panel.add(authorField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("ISBN："), gbc);
        gbc.gridx = 1;
        panel.add(isbnField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("分类："), gbc);
        gbc.gridx = 1;
        panel.add(categoryCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("价格："), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("库存："), gbc);
        gbc.gridx = 1;
        panel.add(stockField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("描述："), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(descArea), gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");
        
        saveButton.addActionListener(e -> {
            try {
                Book newBook = book == null ? new Book() : book;
                newBook.setTitle(titleField.getText());
                newBook.setAuthor(authorField.getText());
                newBook.setIsbn(isbnField.getText());
                newBook.setCategory((String)categoryCombo.getSelectedItem());
                newBook.setPrice(new BigDecimal(priceField.getText()));
                newBook.setStock(Integer.parseInt(stockField.getText()));
                newBook.setDescription(descArea.getText());
                
                if (book == null) {
                    bookService.addBook(newBook);
                } else {
                    bookService.updateBook(newBook);
                }
                
                loadBooks();
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
    
    private void editSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            Book book = tableModel.getBookAt(selectedRow);
            showBookDialog(book);
        } else {
            JOptionPane.showMessageDialog(this, 
                "请选择要编辑的图书", 
                "提示", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void deleteSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            Book book = tableModel.getBookAt(selectedRow);
            int option = JOptionPane.showConfirmDialog(this,
                "确定要删除图书 '" + book.getTitle() + "' 吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION);
                
            if (option == JOptionPane.YES_OPTION) {
                try {
                    bookService.deleteBook(book.getId());
                    loadBooks();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this,
                        "删除失败：" + e.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "请选择要删除的图书",
                "提示",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void searchBooks() {
        try {
            String keyword = searchField.getText().trim();
            String category = categoryCombo.getSelectedItem().toString();
            if (category.equals("全部")) {
                category = null;
            }
            
            List<Book> books = bookService.searchBooks(keyword, category);
            tableModel.setBooks(books);
            sortBooks(); // 应用当前选择的排序
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "搜索失败：" + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void sortBooks() {
        List<Book> books = tableModel.getBooks();
        String sortType = sortCombo.getSelectedItem().toString();
        
        switch (sortType) {
            case "价格升序":
                books.sort((b1, b2) -> b1.getPrice().compareTo(b2.getPrice()));
                break;
            case "价格降序":
                books.sort((b1, b2) -> b2.getPrice().compareTo(b1.getPrice()));
                break;
            case "库存升序":
                books.sort((b1, b2) -> b1.getStock().compareTo(b2.getStock()));
                break;
            case "库存降序":
                books.sort((b1, b2) -> b2.getStock().compareTo(b1.getStock()));
                break;
        }
        
        tableModel.fireTableDataChanged();
    }
    
    private void checkLowStock() {
        try {
            List<Book> lowStockBooks = bookService.getLowStockBooks(10); // 库存低于10的图书
            if (!lowStockBooks.isEmpty()) {
                StringBuilder message = new StringBuilder("以下图书库存不足：\n\n");
                for (Book book : lowStockBooks) {
                    message.append(book.getTitle())
                           .append(" - 当前库存：")
                           .append(book.getStock())
                           .append("\n");
                }
                
                JOptionPane.showMessageDialog(this,
                    message.toString(),
                    "库存预警",
                    JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            // 仅记录错误，不影响界面使用
            e.printStackTrace();
        }
    }
} 