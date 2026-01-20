package com.example.customersystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String email, String otp) {
        // ดักแก้ปัญหา Null Type Safety (ที่พี่เจอในรูปล่าสุด)
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("อีเมลผู้รับห้ามเป็นค่าว่าง");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("sskg82760@gmail.com"); // เมลที่ Verified ใน Brevo
            helper.setTo(email); 
            helper.setSubject("รหัสยืนยันเข้าใช้งาน GSB Portal");
            helper.setText("รหัส OTP ของคุณคือ: <b>" + otp + "</b><br>กรุณาใช้รหัสนี้ภายใน 5 นาที", true);

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ส่งเมลไม่ได้: " + e.getMessage());
        }
    }
}