package com.example.customersystem.service;

import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    public void sendOtpEmail(String email, String otp) {
        // ✅ 1. ย้ายมาอ่านค่าข้างในนี้ เพื่อให้มันดึงค่าล่าสุดจาก Render ทุกครั้งที่เรียกใช้
        String apiKey = System.getenv("BREVO_API_KEY"); 
        
        System.out.println("DEBUG: กำลังส่งเมลหา -> " + email);
        
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("❌ ERROR: BREVO_API_KEY หายไปจากระบบ (null/empty)");
            return; // หยุดทำงานทันทีถ้าไม่มี Key
        }

        String jsonBody = "{"
                + "\"sender\":{\"name\":\"GSB Portal\",\"email\":\"sskg82760@gmail.com\"},"
                + "\"to\":[{\"email\":\"" + email + "\"}],"
                + "\"subject\":\"Your OTP Code: " + otp + "\","
                + "\"htmlContent\":\"<html><body><h3>รหัส OTP ของคุณคือ: <b style='color:blue;'>" + otp + "</b></h3></body></html>\""
                + "}";

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                    .header("api-key", apiKey) // ✅ ใช้ตัวแปรที่ดึงมาสดๆ
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            // ✅ พ่นผลลัพธ์ออกมาดูเลยว่า Brevo ตอบกลับมาว่าอะไร
            System.out.println("Brevo Response Code: " + response.statusCode());
            System.out.println("Brevo Body: " + response.body());

            if (response.statusCode() >= 400) {
                System.err.println("❌ Brevo Error: " + response.body());
            } else {
                System.out.println("✅ OTP Sent Successfully to " + email);
            }
        } catch (Exception e) {
            System.err.println("❌ Fail to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}