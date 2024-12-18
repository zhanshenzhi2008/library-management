package com.bookstore.service;

import com.bookstore.dao.UserDao;
import com.bookstore.model.User;
import com.bookstore.util.ValidationUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

public class UserService {
    private UserDao userDao;
    
    public UserService(Connection conn) {
        this.userDao = new UserDao(conn);
    }
    
    public User login(String username, String password) throws SQLException {
        User user = userDao.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
    
    public void addUser(User user) throws SQLException {
        // 验证用户信息
        validateUser(user);
        
        // 检查用户名是否已存在
        if (userDao.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        userDao.insert(user);
    }
    
    public void updateUser(User user) throws SQLException {
        // 验证用户信息
        validateUser(user);
        
        // 检查用户是否存在
        User existingUser = userDao.findById(user.getId());
        if (existingUser == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        userDao.update(user);
    }
    
    private void validateUser(User user) {
        ValidationUtils.validateUsername(user.getUsername());
        
        if (user.getPassword() != null) {
            ValidationUtils.validatePassword(user.getPassword());
        }
        
        ValidationUtils.validateEmail(user.getEmail());
        ValidationUtils.validatePhone(user.getPhone());
        
        if (user.getRealName() != null && user.getRealName().length() > 50) {
            throw new IllegalArgumentException("真实姓名不能超过50个字符");
        }
    }
    
    public String resetPassword(Integer userId) throws SQLException {
        // 生成随机密码
        String newPassword = generateRandomPassword();
        
        // 更新密码
        userDao.updatePassword(userId, newPassword);
        
        return newPassword;
    }
    
    private String generateRandomPassword() {
        // 生成8位随机密码，包含数字和字母
        String chars = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return sb.toString();
    }
} 