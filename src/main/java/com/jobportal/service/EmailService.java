package com.jobportal.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    public void sendEmail(String to,
                          String subject,
                          String text) {

        try {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);

            headers.setBearerAuth(resendApiKey);

            Map<String, Object> body = Map.of(
                    "from", "onboarding@resend.dev",
                    "to", new String[]{to},
                    "subject", subject,
                    "text", text
            );

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(body, headers);

            String response =
                    restTemplate.postForObject(
                            "https://api.resend.com/emails",
                            entity,
                            String.class
                    );

            System.out.println("Email sent successfully");
            System.out.println(response);

        } catch (Exception e) {

            System.out.println("Email sending failed");

            e.printStackTrace();
        }
    }
}