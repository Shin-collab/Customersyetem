package com.example.customersystem.service;

import com.example.customersystem.model.User;
import com.example.customersystem.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, BCryptPasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public void saveUser(User user) {
        // --- 🛑 จุดที่เพิ่ม: เช็คข้อมูลซ้ำก่อนบันทึก ---
        
        // 1. เช็ค Username ซ้ำ (ยกเว้นกรณีอัปเดตคนเดิม)
        User existingUser = userRepo.findByUsername(user.getUsername());
        if (existingUser != null && !existingUser.getId().equals(user.getId())) {
            throw new RuntimeException("ชื่อผู้ใช้นี้ถูกใช้งานแล้ว กรุณาใช้ชื่ออื่น");
        }

        // 2. เช็ค Email ซ้ำ
        User existingEmail = userRepo.findByEmail(user.getEmail());
        if (existingEmail != null && !existingEmail.getId().equals(user.getId())) {
            throw new RuntimeException("อีเมลนี้ถูกลงทะเบียนไว้แล้ว");
        }

        // --- 🔐 เข้ารหัสผ่านก่อนบันทึก ---
        // เช็คก่อนว่ารหัสที่ส่งมาต้องไม่ว่าง และยังไม่ถูกเข้ารหัส (ความยาวรหัสที่เข้ารหัสแล้วปกติจะ > 30)
        if (user.getPassword() != null && user.getPassword().length() < 30) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

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

    // ฟังก์ชันอัปเดตรหัสผ่าน (ใช้ร่วมกับระบบ OTP ที่เราทำไว้)
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