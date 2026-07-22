package com.jobportal.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jobportal.dto.ApplicationResponse;
import com.jobportal.entity.Application;
import com.jobportal.service.ApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "Job Applications",
    description = "APIs for candidates to apply for jobs, manage resumes, and view submitted applications."
)
@RestController
@RequestMapping("/api/user")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Operation(
        summary = "Apply for a Job",
        description = "Allows an authenticated candidate to apply for a job by uploading a resume."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application submitted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or resume"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Job not found"),
        @ApiResponse(responseCode = "409", description = "Already applied for this job")
    })
    @PostMapping("/apply")
    public Application apply(
            @RequestParam("jobId") Long jobId,
            @RequestParam("file") MultipartFile file,
            Principal principal) {

        if (principal == null) {
            throw new RuntimeException("Unauthorized");
        }

        return applicationService.apply(
                jobId,
                file,
                principal.getName()
        );
    }

    @Operation(
        summary = "Download Resume",
        description = "Downloads the uploaded resume file for a candidate."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resume downloaded successfully"),
        @ApiResponse(responseCode = "404", description = "Resume file not found")
    })
    @GetMapping("/resume")
    public ResponseEntity<Resource> downloadResume(
            @RequestParam String fileName
    ) throws Exception {

        Path path = Paths.get("uploads")
                .resolve(fileName)
                .normalize();

        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Resume file not found: " + fileName);
        }

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\""
                )
                .body(resource);
    }

    @Operation(
        summary = "Preview Resume",
        description = "Displays the uploaded PDF resume directly in the browser."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resume preview loaded successfully"),
        @ApiResponse(responseCode = "404", description = "Resume file not found")
    })
    @GetMapping("/resume/view")
    public ResponseEntity<Resource> previewResume(
            @RequestParam String fileName
    ) throws Exception {

        Path path = Paths.get("uploads")
                .resolve(fileName)
                .normalize();

        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Resume file not found: " + fileName);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(resource);
    }

    @Operation(
        summary = "Get My Applications",
        description = "Retrieves all job applications submitted by the authenticated candidate."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Applications retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/applications")
    public List<ApplicationResponse> getMyApplications(
            Principal principal) {

        return applicationService.getApplicationsByUser(
                principal.getName()
        );
    }
}