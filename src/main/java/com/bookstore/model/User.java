package com.bookstore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String realName;
    private UserRole role;
    private boolean active;
    private LocalDateTime createTime;

    public User(String username, String password, String email, String phone) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.role = UserRole.CUSTOMER;
        this.active = true;
        this.createTime = LocalDateTime.now();
    }
    
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
} 