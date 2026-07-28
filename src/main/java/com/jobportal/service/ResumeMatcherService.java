package com.jobportal.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ResumeMatcherService {

    private static final Logger logger =
            LoggerFactory.getLogger(ResumeMatcherService.class);

    public double calculateMatch(String skills, String resume) {

        try {

            RestTemplate restTemplate = new RestTemplate();

            String url =
                    "https://resume-ai-service-l3qw.onrender.com/predict";

            Map<String, String> request = new HashMap<>();

            request.put("resume", resume);
            request.put("skills", skills);

            logger.info("Calling AI Service...");
            logger.info("URL = {}", url);
            logger.info("Skills = {}", skills);

            Map response =
                    restTemplate.postForObject(
                            url,
                            request,
                            Map.class
                    );

            logger.info("AI Response = {}", response);

            if (response != null &&
                    response.get("match_score") != null) {

                return Double.parseDouble(
                        response.get("match_score").toString()
                );
            }

        } catch (Exception e) {

            logger.error("AI Service Error", e);
        }

        return 50.0;
    }
}