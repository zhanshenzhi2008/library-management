package com.bookstore.ui.model;

import com.bookstore.model.CartItem;
import com.bookstore.model.ShoppingCart;

import javax.swing.table.AbstractTableModel;

public class CartTableModel extends AbstractTableModel {
    private ShoppingCart cart;
    private final String[] columnNames = {"书名", "单价", "数量", "小计"};
    
    public CartTableModel(ShoppingCart cart) {
        this.cart = cart;
    }
    
    public CartItem getItemAt(int row) {
        return cart.getItems().get(row);
    }
    
    @Override
    public int getRowCount() {
        return cart.getItems().size();
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
        CartItem item = cart.getItems().get(rowIndex);
        switch (columnIndex) {
            case 0: return item.getBook().getTitle();
            case 1: return item.getBook().getPrice();
            case 2: return item.getQuantity();
            case 3: return item.getSubtotal();
            default: return null;
        }
    }
} 