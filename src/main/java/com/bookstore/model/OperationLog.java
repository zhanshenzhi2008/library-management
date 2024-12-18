package com.bookstore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLog {
    private Long id;
    private String username;
    private String operation;
    private String details;
    private LocalDateTime timestamp;

    public OperationLog(String username, String operation, String details) {
        this.username = username;
        this.operation = operation;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
} 