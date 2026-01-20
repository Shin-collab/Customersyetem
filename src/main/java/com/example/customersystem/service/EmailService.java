package com.example.customersystem.service;

import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    // 1. พี่ก๊อป API Key จากหน้า Brevo (ที่ขึ้นต้นด้วย xkeysib-...) มาวางในฟันหนูนี้ครับ
    private final String BREVO_API_KEY = "รหัส_API_KEY_จริงของพี่"; 

    public void sendOtpEmail(String email, String otp) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("อีเมลผู้รับห้ามเป็นค่าว่าง");
        }

        // 2. ปรับ Sender ให้เป็นเมลที่พี่ยืนยันโปรไฟล์ไว้ (a06578001@...)
        String jsonBody = "{"
                + "\"sender\":{\"name\":\"GSB Portal\",\"email\":\"a06578001@gmail.com\"},"
                + "\"to\":[{\"email\":\"" + email + "\"}],"
                + "\"subject\":\"รหัสยืนยันเข้าใช้งาน GSB Portal\","
                + "\"htmlContent\":\"<html><body><h3>รหัส OTP ของคุณคือ: <b style='color:blue;'>" + otp + "</b></h3><p>กรุณาใช้รหัสนี้ภายใน 5 นาทีครับ</p></body></html>\""
                + "}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                .header("api-key", BREVO_API_KEY) // ตัวนี้ห้ามมีภาษาไทยเด็ดขาด
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