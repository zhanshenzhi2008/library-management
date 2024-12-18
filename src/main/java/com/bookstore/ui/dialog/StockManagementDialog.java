package com.bookstore.ui.dialog;

import com.bookstore.model.Book;
import com.bookstore.model.StockAdjustment;
import com.bookstore.service.BookService;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StockManagementDialog extends JDialog {
    private BookService bookService;
    private Book currentBook;
    private JSpinner stockSpinner;
    private JTextArea importPreviewArea;
    
    public StockManagementDialog(Frame owner, BookService bookService, Book book) {
        super(owner, "库存管理 - " + book.getTitle(), true);
        this.bookService = bookService;
        this.currentBook = book;
        
        setSize(500, 400);
        setLocationRelativeTo(owner);
        initComponents();
    }
    
    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // 库存调整面板
        JPanel adjustPanel = new JPanel(new BorderLayout(10, 10));
        adjustPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        infoPanel.add(new JLabel("当前库存："));
        infoPanel.add(new JLabel(String.valueOf(currentBook.getStock())));
        infoPanel.add(new JLabel("调整数量："));
        
        stockSpinner = new JSpinner(new SpinnerNumberModel(0, -999, 999, 1));
        infoPanel.add(stockSpinner);
        
        JButton adjustButton = new JButton("调整库存");
        adjustButton.addActionListener(e -> adjustStock());
        
        adjustPanel.add(infoPanel, BorderLayout.NORTH);
        adjustPanel.add(adjustButton, BorderLayout.SOUTH);
        
        // 批量导入面板
        JPanel importPanel = new JPanel(new BorderLayout(10, 10));
        importPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel importButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton chooseFileButton = new JButton("选择文件");
        JButton importButton = new JButton("导入");
        JButton templateButton = new JButton("下载模板");
        
        chooseFileButton.addActionListener(e -> chooseFile());
        importButton.addActionListener(e -> importStock());
        templateButton.addActionListener(e -> downloadTemplate());
        
        importButtonPanel.add(chooseFileButton);
        importButtonPanel.add(importButton);
        importButtonPanel.add(templateButton);
        
        importPreviewArea = new JTextArea();
        importPreviewArea.setEditable(false);
        
        importPanel.add(importButtonPanel, BorderLayout.NORTH);
        importPanel.add(new JScrollPane(importPreviewArea), BorderLayout.CENTER);
        
        tabbedPane.addTab("库存调整", adjustPanel);
        tabbedPane.addTab("批量导入", importPanel);
        
        add(tabbedPane);
    }
    
    private void adjustStock() {
        try {
            int adjustment = (Integer) stockSpinner.getValue();
            if (adjustment == 0) {
                return;
            }
            
            int newStock = currentBook.getStock() + adjustment;
            if (newStock < 0) {
                JOptionPane.showMessageDialog(this,
                    "库存不能小于0",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            bookService.adjustStock(currentBook.getId(), adjustment);
            currentBook.setStock(newStock);
            
            JOptionPane.showMessageDialog(this,
                "库存调整成功",
                "提示",
                JOptionPane.INFORMATION_MESSAGE);
                
            stockSpinner.setValue(0);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "库存调整失败：" + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "CSV文件", "csv"));
            
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            previewImportFile(file);
        }
    }
    
    private void previewImportFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder preview = new StringBuilder();
            String line;
            int lineCount = 0;
            
            while ((line = reader.readLine()) != null && lineCount < 10) {
                preview.append(line).append("\n");
                lineCount++;
            }
            
            if (reader.readLine() != null) {
                preview.append("...\n");
            }
            
            importPreviewArea.setText(preview.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "读取文件失败：" + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void importStock() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "CSV文件", "csv"));
            
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                List<StockAdjustment> adjustments = parseImportFile(file);
                bookService.batchAdjustStock(adjustments);
                
                JOptionPane.showMessageDialog(this,
                    "批量导入成功",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "导入失败：" + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private List<StockAdjustment> parseImportFile(File file) throws IOException {
        List<StockAdjustment> adjustments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1) { // 跳过标题行
                    continue;
                }
                
                String[] fields = line.split(",");
                if (fields.length != 2) {
                    throw new IllegalArgumentException("第" + lineNumber + "行格式错误");
                }
                
                try {
                    String isbn = fields[0].trim();
                    int adjustment = Integer.parseInt(fields[1].trim());
                    adjustments.add(new StockAdjustment(isbn, adjustment));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("第" + lineNumber + "行数据格式错误");
                }
            }
        }
        return adjustments;
    }
    
    private void downloadTemplate() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "CSV文件", "csv"));
        fileChooser.setSelectedFile(new File("stock_template.csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("ISBN,调整数量");
                writer.println("9787111111111,10");
                writer.println("9787222222222,-5");
                
                JOptionPane.showMessageDialog(this,
                    "模板下载成功",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "模板下载失败：" + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 