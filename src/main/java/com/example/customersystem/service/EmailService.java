package com.example.customersystem.service;

import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    // 1. ใส่ API Key จากหน้า Brevo (SMTP & API -> API Keys)
    private final String BREVO_API_KEY = "ใส่_API_KEY_ของพี่ตรงนี้"; 

    public void sendOtpEmail(String email, String otp) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("อีเมลผู้รับห้ามเป็นค่าว่าง");
        }

        // 2. สร้างโครงสร้างข้อมูล JSON สำหรับส่ง (ใช้ Sender ที่พี่ยืนยันไว้ใน Brevo)
        String jsonBody = "{"
                + "\"sender\":{\"name\":\"GSB Portal\",\"email\":\"a06578001@smtp-brevo.com\"},"
                + "\"to\":[{\"email\":\"" + email + "\"}],"
                + "\"subject\":\"รหัสยืนยันเข้าใช้งาน GSB Portal\","
                + "\"htmlContent\":\"<html><body><h3>รหัส OTP ของคุณคือ: <b style='color:blue;'>" + otp + "</b></h3><p>กรุณาใช้รหัสนี้ภายใน 5 นาทีครับ</p></body></html>\""
                + "}";

        // 3. ใช้ HttpClient (มีมากับ Java 11+ อยู่แล้ว ไม่ต้องเพิ่ม dependency)
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                .header("api-key", BREVO_API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() >= 400) {
                System.err.println("ส่งผ่าน API ไม่สำเร็จ: " + response.body());
                throw new RuntimeException("Brevo API Error: " + response.body());
            } else {
                System.out.println("✅ ส่งเมลสำเร็จแล้วผ่าน Brevo API!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("เกิดข้อผิดพลาดในการเรียก API: " + e.getMessage());
        }
    }
}