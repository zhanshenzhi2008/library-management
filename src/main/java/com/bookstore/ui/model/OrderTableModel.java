package com.bookstore.ui.model;

import com.bookstore.model.Order;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class OrderTableModel extends AbstractTableModel {
    private List<Order> orders = new ArrayList<>();
    private final String[] columnNames = {"订单编号", "下单时间", "订单状态", "订单金额"};
    
    public void setOrders(List<Order> orders) {
        this.orders = orders;
        fireTableDataChanged();
    }
    
    public Order getOrderAt(int row) {
        return orders.get(row);
    }
    
    @Override
    public int getRowCount() {
        return orders.size();
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
        Order order = orders.get(rowIndex);
        switch (columnIndex) {
            case 0: return order.getId();
            case 1: return order.getCreateTime();
            case 2: return order.getStatus();
            case 3: return order.getTotalAmount();
            default: return null;
        }
    }
} 