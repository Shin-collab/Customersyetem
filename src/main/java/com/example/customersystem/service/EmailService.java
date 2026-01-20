package com.example.customersystem.service;

import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    private final String BREVO_API_KEY = "YOUR_ACTUAL_API_KEY_HERE"; 

    public void sendOtpEmail(String email, String otp) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Recipient email cannot be null or empty");
        }

        String jsonBody = "{"
                + "\"sender\":{\"name\":\"GSB Portal\",\"email\":\"a06578001@gmail.com\"},"
                + "\"to\":[{\"email\":\"" + email + "\"}],"
                + "\"subject\":\"Your OTP Code is " + otp + "\","
                + "\"htmlContent\":\"<html><body><h3>Your OTP: <b style='color:blue;'>" + otp + "</b></h3><p>Valid for 5 minutes.</p></body></html>\""
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
                System.out.println("✅ Email sent successfully via API!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Connection Error: " + e.getMessage());
        }
    }
}