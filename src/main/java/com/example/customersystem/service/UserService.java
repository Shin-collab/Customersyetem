package com.example.customersystem.service;

import com.example.customersystem.model.User;
import com.example.customersystem.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 1. กุเพิ่มตัวนี้เพื่อใช้ระบบ Rollback

@Service
public class UserService {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, BCryptPasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional 
    public void saveUser(User user) {
        
        // เช็คชื่อซ้ำ
        User existingUser = userRepo.findByUsername(user.getUsername());
        if (existingUser != null && !existingUser.getId().equals(user.getId())) {
            throw new RuntimeException("ชื่อผู้ใช้นี้ถูกใช้งานแล้ว กรุณาใช้ชื่ออื่น");
        }

        // เช็คอีเมลซ้ำ
        User existingEmail = userRepo.findByEmail(user.getEmail());
        if (existingEmail != null && !existingEmail.getId().equals(user.getId())) {
            throw new RuntimeException("อีเมลนี้ถูกลงทะเบียนไว้แล้ว");
        }

        // เข้ารหัสผ่าน
        if (user.getPassword() != null && user.getPassword().length() < 30) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // บันทึกข้อมูล
        userRepo.save(user); 
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Transactional 
    public boolean updatePassword(String email, String oldPassword, String newPassword) {
        User user = userRepo.findByEmail(email);
        if (user != null && passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepo.save(user);
            return true;
        }
        return false;
    }
}