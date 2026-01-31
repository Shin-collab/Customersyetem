package com.example.customersystem.service;

import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    public void sendOtpEmail(String email, String otp) {
        // ดึง API Key ทุกครั้งที่เรียกใช้
        String apiKey = System.getenv("BREVO_API_KEY"); 
        
        System.out.println("🚀 [LOG] เริ่มขั้นตอนส่งเมลหา: " + email);
        
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("❌ [ERROR] ไม่เจอ BREVO_API_KEY ใน Environment!");
            return; 
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
                    .header("api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            // พ่นผลลัพธ์เพื่อวิเคราะห์
            System.out.println("📡 HTTP Status: " + response.statusCode());
            System.out.println("📡 Response Body: " + response.body());

            if (response.statusCode() >= 400) {
                System.err.println("❌ Brevo Rejected: " + response.body());
            } else {
                System.out.println("✅ [SUCCESS] เมลส่งออกไปเรียบร้อยแล้ว!");
            }
        } catch (Exception e) {
            System.err.println("❌ [CRITICAL ERROR]: " + e.getMessage());
            e.printStackTrace();
        }
    }
}