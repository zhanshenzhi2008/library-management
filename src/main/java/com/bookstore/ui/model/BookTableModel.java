package com.bookstore.ui.model;

import com.bookstore.model.Book;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class BookTableModel extends AbstractTableModel {
    private List<Book> books = new ArrayList<>();
    private final String[] columnNames = {"ID", "ISBN", "书名", "作者", "出版社", "价格", "库存"};
    
    public void setBooks(List<Book> books) {
        this.books = books;
        fireTableDataChanged();
    }
    
    public Book getBookAt(int row) {
        return books.get(row);
    }

    public List<Book> getBooks() {
        return books;
    }
    
    @Override
    public int getRowCount() {
        return books.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Book book = books.get(rowIndex);
        switch (columnIndex) {
            case 0: return book.getId();
            case 1: return book.getIsbn();
            case 2: return book.getTitle();
            case 3: return book.getAuthor();
            case 4: return book.getPublisher();
            case 5: return book.getPrice();
            case 6: return book.getStock();
            default: return null;
        }
    }
} 