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

    // สำหรับบันทึกผู้ใช้ใหม่ (สมัครสมาชิก)
    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
    }

    // เพิ่มอันนี้: สำหรับหาข้อมูลผู้ใช้ด้วย Email (เอาไว้ดึงชื่อมาโชว์ที่หน้าแรก)
    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    // เพิ่มอันนี้: สำหรับระบบเปลี่ยนรหัสผ่านภายในเว็บ
    public boolean updatePassword(String email, String oldPassword, String newPassword) {
        User user = userRepo.findByEmail(email);
        
        // เช็คก่อนว่ารหัสผ่านเดิมที่กรอกมา ถูกต้องตรงกับในฐานข้อมูลไหม
        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            // ถ้ารหัสเดิมถูก ให้เข้ารหัสอันใหม่แล้วบันทึกทับลงไป
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepo.save(user);
            return true; // เปลี่ยนสำเร็จ
        }
        return false; // รหัสเดิมผิด เปลี่ยนไม่ได้
    }
}