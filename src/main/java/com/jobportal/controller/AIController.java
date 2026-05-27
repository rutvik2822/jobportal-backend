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

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final OpenRouterService openRouterService;

    private final PdfService pdfService;

    public AIController(

            OpenRouterService openRouterService,

            PdfService pdfService) {

        this.openRouterService =
                openRouterService;

        this.pdfService =
                pdfService;
    }

    // =====================================
    // JOB ANALYSIS
    // =====================================

    @PostMapping("/analyze-job")

    public Object analyzeJob(

            @RequestBody Map<String, String> body) {

        String jobText =
                body.get("job");

        return openRouterService
                .analyzeJob(jobText);
    }

    // =====================================
    // REAL RESUME ANALYSIS
    // =====================================

    @PostMapping("/analyze-resume")

    public Object analyzeResume(

            @RequestParam("file")
            MultipartFile file) {

        try {

            String extractedText =

                    pdfService.extractText(
                            file.getInputStream()
                    );

            return openRouterService
                    .analyzeResume(extractedText);

        } catch (Exception e) {

            e.printStackTrace();

            return "Resume analysis failed";
        }
    }

    // =====================================
    // REAL RESUME VS JOB COMPARISON
    // =====================================

    @PostMapping("/compare")

    public Object compareResumeWithJob(

            @RequestParam("file")
            MultipartFile file,

            @RequestParam("job")
            String jobText) {

        try {

            String extractedText =

                    pdfService.extractText(
                            file.getInputStream()
                    );

            return openRouterService
                    .compareResumeWithJob(
                            extractedText,
                            jobText
                    );

        } catch (Exception e) {

            e.printStackTrace();

            return "Comparison failed";
        }
    }
}