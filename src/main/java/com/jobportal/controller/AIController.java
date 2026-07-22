package com.jobportal.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jobportal.service.OpenRouterService;
import com.jobportal.service.PdfService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(
    name = "AI Resume Analysis",
    description = "APIs for AI-powered job analysis, resume analysis, and resume-job matching using OpenRouter AI."
)
@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final OpenRouterService openRouterService;
    private final PdfService pdfService;

    public AIController(
            OpenRouterService openRouterService,
            PdfService pdfService) {

        this.openRouterService = openRouterService;
        this.pdfService = pdfService;
    }

    // =====================================
    // JOB ANALYSIS
    // =====================================

    @Operation(
        summary = "Analyze Job Description",
        description = "Analyzes a job description using AI and provides insights such as required skills, responsibilities, and recommendations."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Job analyzed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid job description")
    })
    @PostMapping("/analyze-job")
    public Object analyzeJob(
            @RequestBody Map<String, String> body) {

        String jobText = body.get("job");

        return openRouterService.analyzeJob(jobText);
    }

    // =====================================
    // REAL RESUME ANALYSIS
    // =====================================

    @Operation(
        summary = "Analyze Resume",
        description = "Uploads a PDF resume, extracts its content, and performs AI-powered resume analysis."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resume analyzed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or unsupported resume file"),
        @ApiResponse(responseCode = "500", description = "Resume analysis failed")
    })
    @PostMapping("/analyze-resume")
    public Object analyzeResume(
            @RequestParam("file") MultipartFile file) {

        try {

            String extractedText =
                    pdfService.extractText(file.getInputStream());

            return openRouterService.analyzeResume(extractedText);

        } catch (Exception e) {

            e.printStackTrace();
            return "Resume analysis failed";
        }
    }

    // =====================================
    // REAL RESUME VS JOB COMPARISON
    // =====================================

    @Operation(
        summary = "Compare Resume with Job",
        description = "Uploads a resume and compares it with a job description using AI to determine compatibility and matching score."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comparison completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid resume or job description"),
        @ApiResponse(responseCode = "500", description = "Comparison failed")
    })
    @PostMapping("/compare")
    public Object compareResumeWithJob(
            @RequestParam("file") MultipartFile file,
            @RequestParam("job") String jobText) {

        try {

            String extractedText =
                    pdfService.extractText(file.getInputStream());

            return openRouterService.compareResumeWithJob(
                    extractedText,
                    jobText);

        } catch (Exception e) {

            e.printStackTrace();
            return "Comparison failed";
        }
    }
}