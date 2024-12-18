package com.bookstore.ui.model;

import com.bookstore.model.User;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class UserTableModel extends AbstractTableModel {
    private List<User> users = new ArrayList<>();
    private final String[] columnNames = {"ID", "用户名", "真实姓名", "电话", "邮箱", "管理员"};
    
    public void setUsers(List<User> users) {
        this.users = users;
        fireTableDataChanged();
    }
    
    public User getUserAt(int row) {
        return users.get(row);
    }
    
    @Override
    public int getRowCount() {
        return users.size();
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
        User user = users.get(rowIndex);
        switch (columnIndex) {
            case 0: return user.getId();
            case 1: return user.getUsername();
            case 2: return user.getRealName();
            case 3: return user.getPhone();
            case 4: return user.getEmail();
            case 5: return user.isAdmin() ? "是" : "否";
            default: return null;
        }
    }
} 