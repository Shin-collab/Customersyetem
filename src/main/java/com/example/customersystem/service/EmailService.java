package com.example.customersystem.service;

import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    public void sendOtpEmail(String email, String otp) {
        String apiKey = System.getenv("BREVO_API_KEY"); 
        
        System.out.println("📡 [EmailService] เริ่มส่งเมลไปที่: " + email);
        
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("❌ [Error] BREVO_API_KEY หาย!");
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
            
            System.out.println("📡 Brevo Response Status: " + response.statusCode());
            System.out.println("📡 Brevo Response Body: " + response.body());

            if (response.statusCode() >= 400) {
                System.err.println("❌ [Brevo API Error]: " + response.body());
            } else {
                System.out.println("✅ [SUCCESS] OTP ส่งเรียบร้อย!");
            }
        } catch (Exception e) {
            System.err.println("❌ [Critical Error]: " + e.getMessage());
            e.printStackTrace();
        }
    }
}