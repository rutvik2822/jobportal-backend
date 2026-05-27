package com.jobportal.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class OpenRouterService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://openrouter.ai")
            .defaultHeader(
                    HttpHeaders.CONTENT_TYPE,
                    MediaType.APPLICATION_JSON_VALUE
            )
            .build();

    // ==============================
    // JOB ANALYSIS
    // ==============================

    public Object analyzeJob(String jobText) {

        String prompt = """
        You are an AI Job Market Expert.

        Analyze this JOB REQUIREMENT and provide:

        1. Job Overview
        2. Required Technical Skills
        3. Required Soft Skills
        4. Experience Level Needed
        5. Important Technologies To Learn
        6. Interview Preparation Tips
        7. Career Advice For Candidates
        8. Expected Responsibilities
        9. Difficulty Level Of This Job
        10. Final Recommendation

        Job Details:
        """ + jobText;

        return sendRequest(prompt);
    }

    // ==============================
    // RESUME ANALYSIS
    // ==============================

    public Object analyzeResume(String resumeText) {

        String prompt = """
        You are an AI Resume Reviewer.

        Analyze this RESUME and provide:

        1. Professional Summary
        2. Technical Skills Found
        3. Strong Points
        4. Missing Skills
        5. ATS Improvement Suggestions
        6. Resume Formatting Suggestions
        7. Project Improvement Suggestions
        8. Career Recommendations
        9. Interview Preparation Tips
        10. Final ATS Readiness Score out of 100

        Resume:
        """ + resumeText;

        return sendRequest(prompt);
    }

    // ==============================
    // RESUME VS JOB MATCH ANALYSIS
    // ==============================

    public Object compareResumeWithJob(
            String resumeText,
            String jobText) {

        String prompt = """
        You are an AI ATS System.

        Compare this RESUME with the JOB REQUIREMENT.

        Provide:

        1. Match Percentage
        2. Matching Skills
        3. Missing Skills
        4. Important Keywords Missing
        5. Resume Improvements Required
        6. Chances Of Selection
        7. Technical Areas To Improve
        8. Final Recommendation

        JOB REQUIREMENT:
        """
        + jobText +

        """

        RESUME:
        """
        + resumeText;

        return sendRequest(prompt);
    }

    // ==============================
    // COMMON API METHOD
    // ==============================

    private Object sendRequest(String prompt) {

        Map<String, Object> requestBody = Map.of(

                "model", "openai/gpt-3.5-turbo",

                "messages", List.of(

                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );

        try {

            Object response = webClient.post()

                    .uri("/api/v1/chat/completions")

                    .header(
                            "Authorization",
                            "Bearer " + apiKey
                    )

                    .header(
                            "HTTP-Referer",
                            "http://localhost:3000"
                    )

                    .header(
                            "X-Title",
                            "AI Job Portal"
                    )

                    .bodyValue(requestBody)

                    .retrieve()

                    .bodyToMono(Object.class)

                    .block();

            return response;

        } catch (Exception e) {

            e.printStackTrace();

            return "AI Error: " + e.getMessage();
        }
    }
}