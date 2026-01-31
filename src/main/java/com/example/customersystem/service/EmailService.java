package com.example.customersystem.service;

import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    private final String BREVO_API_KEY = System.getenv("BREVO_API_KEY"); 

    public void sendOtpEmail(String email, String otp) {
        if (BREVO_API_KEY == null || BREVO_API_KEY.isEmpty()) {
            throw new RuntimeException("ERROR: BREVO_API_KEY is missing!");
        }

        String jsonBody = "{"
                + "\"sender\":{\"name\":\"GSB Portal\",\"email\":\"sskg82760@gmail.com\"},"
                + "\"to\":[{\"email\":\"" + email + "\"}],"
                + "\"subject\":\"Your OTP Code: " + otp + "\","
                + "\"htmlContent\":\"<html><body><h3>รหัส OTP ของคุณคือ: <b style='color:blue;'>" + otp + "</b></h3></body></html>\""
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
                System.err.println("❌ Error: " + response.body());
                throw new RuntimeException("Brevo Error: " + response.body());
            }
            System.out.println("✅ OTP Sent to " + email);
        } catch (Exception e) {
            throw new RuntimeException("Fail: " + e.getMessage());
        }
    }
}