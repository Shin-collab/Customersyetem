package com.example.customersystem.service;

import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    // ✅ ดึงค่ารหัสจาก Railway Variables
    private final String BREVO_API_KEY = System.getenv("BREVO_API_KEY"); 

    public void sendOtpEmail(String email, String otp) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Recipient email cannot be null or empty");
        }

        // ✅ เปลี่ยน email ผู้ส่งจาก a06578001 เป็น sskg82760@gmail.com (เมลของพี่เอง)
        String jsonBody = "{"
                + "\"sender\":{\"name\":\"GSB Portal\",\"email\":\"sskg82760@gmail.com\"},"
                + "\"to\":[{\"email\":\"" + email + "\"}],"
                + "\"subject\":\"Your OTP Code: " + otp + "\","
                + "\"htmlContent\":\"<html><body><h3>รหัส OTP ของคุณคือ: <b style='color:blue;'>" + otp + "</b></h3><p>รหัสนี้จะหมดอายุใน 5 นาที</p></body></html>\""
                + "}";

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
                System.err.println("API Error: " + response.body());
                throw new RuntimeException("Brevo API Error: " + response.body());
            } else {
                System.out.println("✅ ส่งเมลสำเร็จ! ผ่านเมล sskg82760 เรียบร้อย");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Connection Error: " + e.getMessage());
        }
    }
}