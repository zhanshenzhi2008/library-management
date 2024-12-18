package com.bookstore.dao;

import com.bookstore.model.OperationLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OperationLogDAO {
    private Connection conn;
    
    public OperationLogDAO(Connection conn) {
        this.conn = conn;
    }
    
    public void createLog(OperationLog log) {
        String sql = "INSERT INTO operation_logs (username, operation, details, timestamp) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, log.getUsername());
            pstmt.setString(2, log.getOperation());
            pstmt.setString(3, log.getDetails());
            pstmt.setTimestamp(4, Timestamp.valueOf(log.getTimestamp()));
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    log.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<OperationLog> getLogs(int limit, int offset) {
        List<OperationLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM operation_logs ORDER BY timestamp DESC LIMIT ? OFFSET ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OperationLog log = new OperationLog();
                    log.setId(rs.getLong("id"));
                    log.setUsername(rs.getString("username"));
                    log.setOperation(rs.getString("operation"));
                    log.setDetails(rs.getString("details"));
                    log.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                    logs.add(log);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    public List<OperationLog> getLogsByUser(String username) {
        List<OperationLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM operation_logs WHERE username = ? ORDER BY timestamp DESC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OperationLog log = new OperationLog();
                    log.setId(rs.getLong("id"));
                    log.setUsername(rs.getString("username"));
                    log.setOperation(rs.getString("operation"));
                    log.setDetails(rs.getString("details"));
                    log.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                    logs.add(log);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
} 