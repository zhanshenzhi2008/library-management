package com.bookstore.model;

public enum UserRole {
    ADMIN("管理员"),
    LIBRARIAN("图书管理员"),
    CUSTOMER("普通用户");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 