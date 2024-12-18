package com.bookstore.util;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {
    private Connection connection;
    
    public TransactionManager(Connection connection) {
        this.connection = connection;
    }
    
    public void beginTransaction() throws SQLException {
        connection.setAutoCommit(false);
    }
    
    public void commit() throws SQLException {
        try {
            connection.commit();
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    public void rollback() throws SQLException {
        try {
            connection.rollback();
        } finally {
            connection.setAutoCommit(true);
        }
    }
} 