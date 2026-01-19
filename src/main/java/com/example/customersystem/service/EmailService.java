package com.example.customersystem.service; // แก้เป็นชื่อโปรเจกต์ของพี่แล้ว

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sskg82760@gmail.com");
        message.setTo(toEmail);
        message.setSubject("รหัสยืนยันเข้าใช้งาน GSB Portal");
        message.setText("รหัส OTP ของคุณคือ: " + otp + "\nกรุณาใช้รหัสนี้เพื่อยืนยันตัวตนภายใน 5 นาที");
        mailSender.send(message);
    }
}