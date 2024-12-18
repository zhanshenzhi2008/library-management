package com.bookstore.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@(.+)$");
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^1[3-9]\\d{9}$");
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_]{4,20}$");
    
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$");
    
    private static final Pattern ISBN_PATTERN = Pattern.compile(
        "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$");
    
    public static void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException("用户名必须是4-20位字母、数字或下划线");
        }
    }
    
    public static void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("密码必须包含字母和数字，长度6-20位");
        }
    }
    
    public static void validateEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                throw new IllegalArgumentException("邮箱格式不正确");
            }
        }
    }
    
    public static void validatePhone(String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                throw new IllegalArgumentException("手机号格式不正确");
            }
        }
    }
    
    public static void validateISBN(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN不能为空");
        }
        if (!ISBN_PATTERN.matcher(isbn).matches()) {
            throw new IllegalArgumentException("ISBN格式不正确");
        }
    }
    
    public static void validatePrice(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("价格不能为空");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("价格必须大于0");
        }
        if (price.scale() > 2) {
            throw new IllegalArgumentException("价格最多保留两位小数");
        }
    }
    
    public static void validateStock(Integer stock) {
        if (stock == null) {
            throw new IllegalArgumentException("库存不能为空");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("库存不能为负数");
        }
    }
    
    public static void validateBookTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("图书标题不能为空");
        }
        if (title.length() > 100) {
            throw new IllegalArgumentException("图书标题不能超过100个字符");
        }
    }
} 