package com.example.customersystem.repository;

import com.example.customersystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // สำคัญสุด: ใช้หา User ตอน Login และดึงข้อมูลโปรไฟล์
    User findByUsername(String username);

    // ใช้สำหรับระบบกู้คืนรหัสผ่าน (ถ้ามีระบบส่ง Email)
    User findByEmail(String email);
    
    // ใช้สำหรับ Verify Token ตอน Reset Password
    User findByResetPasswordToken(String token);
}