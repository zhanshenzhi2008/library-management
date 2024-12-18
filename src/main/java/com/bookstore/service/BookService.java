package com.bookstore.service;

import com.bookstore.dao.BookDao;
import com.bookstore.model.Book;
import com.bookstore.model.StockAdjustment;
import com.bookstore.util.ValidationUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookService {
    private BookDao bookDao;
    
    public BookService(Connection conn) {
        this.bookDao = new BookDao(conn);
    }
    
    public void addBook(Book book) throws SQLException {
        // 验证图书信息
        validateBook(book);
        
        // 检查ISBN是否已存在
        if (bookDao.findByIsbn(book.getIsbn()) != null) {
            throw new IllegalArgumentException("ISBN已存在");
        }
        
        bookDao.insert(book);
    }
    
    public void updateBook(Book book) throws SQLException {
        // 验证图书信息
        validateBook(book);
        
        // 检查图书是否存在
        Book existingBook = bookDao.findById(book.getId());
        if (existingBook == null) {
            throw new IllegalArgumentException("图书不存在");
        }
        
        // 如果ISBN变更，检查新ISBN是否已存在
        if (!existingBook.getIsbn().equals(book.getIsbn())) {
            if (bookDao.findByIsbn(book.getIsbn()) != null) {
                throw new IllegalArgumentException("ISBN已存在");
            }
        }
        
        bookDao.update(book);
    }
    
    private void validateBook(Book book) {
        ValidationUtils.validateBookTitle(book.getTitle());
        ValidationUtils.validateISBN(book.getIsbn());
        ValidationUtils.validatePrice(book.getPrice());
        ValidationUtils.validateStock(book.getStock());
        
        if (book.getAuthor() != null && book.getAuthor().length() > 50) {
            throw new IllegalArgumentException("作者名不能超过50个字符");
        }
        
        if (book.getDescription() != null && book.getDescription().length() > 1000) {
            throw new IllegalArgumentException("图书描述不能超过1000个字符");
        }
    }
    
    public List<Book> findBooksByCategory(String category) throws SQLException {
        return bookDao.findByCategory(category);
    }
    
    public List<Book> searchBooks(String keyword, String category) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM books WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (title LIKE ? OR author LIKE ? OR isbn LIKE ?)");
            String likePattern = "%" + keyword + "%";
            params.add(likePattern);
            params.add(likePattern);
            params.add(likePattern);
        }
        
        if (category != null && !category.isEmpty()) {
            sql.append(" AND category = ?");
            params.add(category);
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            List<Book> books = new ArrayList<>();
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
            return books;
        }
    }
    
    public List<Book> getLowStockBooks(int threshold) throws SQLException {
        String sql = "SELECT * FROM books WHERE stock < ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, threshold);
            ResultSet rs = stmt.executeQuery();
            List<Book> books = new ArrayList<>();
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
            return books;
        }
    }
    
    public void adjustStock(Integer bookId, int adjustment) throws SQLException {
        String sql = "UPDATE books SET stock = stock + ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, adjustment);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
        }
    }
    
    public void batchAdjustStock(List<StockAdjustment> adjustments) throws SQLException {
        String findSql = "SELECT id, stock FROM books WHERE isbn = ?";
        String updateSql = "UPDATE books SET stock = ? WHERE id = ?";
        
        conn.setAutoCommit(false);
        try {
            for (StockAdjustment adj : adjustments) {
                // 查找图书
                try (PreparedStatement stmt = conn.prepareStatement(findSql)) {
                    stmt.setString(1, adj.getIsbn());
                    ResultSet rs = stmt.executeQuery();
                    if (!rs.next()) {
                        throw new IllegalArgumentException("找不到ISBN为" + adj.getIsbn() + "的图书");
                    }
                    
                    int id = rs.getInt("id");
                    int currentStock = rs.getInt("stock");
                    int newStock = currentStock + adj.getAdjustment();
                    
                    if (newStock < 0) {
                        throw new IllegalArgumentException(
                            "ISBN为" + adj.getIsbn() + "的��书库存不足，无法调整");
                    }
                    
                    // 更新库存
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, newStock);
                        updateStmt.setInt(2, id);
                        updateStmt.executeUpdate();
                    }
                }
            }
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
} 